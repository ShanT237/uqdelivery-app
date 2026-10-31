import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';
import { authInterceptor } from './auth.interceptor';

const API = 'http://localhost:8080/api/v1';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let navigate: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    localStorage.clear();
    localStorage.setItem('uq-sesion', JSON.stringify({
      token: 'token-de-prueba', email: 'test@uq.com', fullName: 'Test', role: 'USER'
    }));

    navigate = vi.fn();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: { navigate } }
      ]
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('agrega el header Authorization con el token', () => {
    http.post(`${API}/envios`, {}).subscribe();
    const req = httpMock.expectOne(`${API}/envios`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-de-prueba');
    req.flush({});
  });

  it('NO envía el token a servicios externos', () => {
    http.get('https://api.externa.com/mapas').subscribe();
    const req = httpMock.expectOne('https://api.externa.com/mapas');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });

  it('NO envía el token a una URL que solo se parece a la de la API', () => {
    http.get('http://localhost:8080/api/v1-falsa/datos').subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/v1-falsa/datos');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });

  it('un 401 de un servicio externo no cierra la sesión', () => {
    http.get('https://api.externa.com/mapas').subscribe({ error: () => {} });
    httpMock.expectOne('https://api.externa.com/mapas').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(localStorage.getItem('uq-sesion')).not.toBeNull();
    expect(navigate).not.toHaveBeenCalled();
  });

  for (const status of [401, 403]) {
    it(`con ${status} cierra la sesión y redirige a /login`, () => {
      http.post(`${API}/envios`, {}).subscribe({ error: () => {} });
      httpMock.expectOne(`${API}/envios`).flush(null, { status, statusText: 'Error' });

      expect(localStorage.getItem('uq-sesion')).toBeNull();
      expect(navigate).toHaveBeenCalledWith(['/login'], { queryParams: { sesion: 'expirada' } });
    });
  }

  it('un 401 del login (credenciales inválidas) NO cierra sesión ni redirige', () => {
    http.post(`${API}/auth/login`, {}).subscribe({ error: () => {} });
    httpMock.expectOne(`${API}/auth/login`).flush(
      { message: 'Credenciales inválidas' }, { status: 401, statusText: 'Unauthorized' });

    expect(localStorage.getItem('uq-sesion')).not.toBeNull();
    expect(navigate).not.toHaveBeenCalled();
  });

  it('otros errores (500) no cierran la sesión', () => {
    http.post(`${API}/envios`, {}).subscribe({ error: () => {} });
    httpMock.expectOne(`${API}/envios`).flush(null, { status: 500, statusText: 'Error' });

    expect(localStorage.getItem('uq-sesion')).not.toBeNull();
    expect(navigate).not.toHaveBeenCalled();
  });
});
