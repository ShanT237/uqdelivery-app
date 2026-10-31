import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse } from '../models/auth.model';

const API_AUTH = 'http://localhost:8080/api/v1/auth';

// JWT falso (sin firma válida) con el "exp" indicado, en segundos
function tokenConExp(exp: number): string {
  return `header.${btoa(JSON.stringify({ sub: 'ana@uq.com', exp }))}.firma`;
}

const respuesta: AuthResponse = {
  token: tokenConExp(Math.floor(Date.now() / 1000) + 3600),
  email: 'ana@uq.com',
  fullName: 'Ana Pérez',
  role: 'USER'
};

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  function crearServicio(): void {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  }

  beforeEach(() => localStorage.clear());
  afterEach(() => httpMock.verify());

  it('login envía las credenciales y guarda la sesión', () => {
    crearServicio();
    service.login({ email: 'ana@uq.com', password: 'clave12345' }).subscribe();

    const req = httpMock.expectOne(`${API_AUTH}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'ana@uq.com', password: 'clave12345' });
    req.flush(respuesta);

    expect(service.usuario()).toEqual(respuesta);
    expect(service.token).toBe(respuesta.token);
    expect(JSON.parse(localStorage.getItem('uq-sesion')!)).toEqual(respuesta);
  });

  it('registrar envía los datos y guarda la sesión', () => {
    crearServicio();
    service.registrar({ fullName: 'Ana Pérez', email: 'ana@uq.com', password: 'clave12345' }).subscribe();

    const req = httpMock.expectOne(`${API_AUTH}/register`);
    expect(req.request.body.fullName).toBe('Ana Pérez');
    req.flush(respuesta);

    expect(service.usuario()?.fullName).toBe('Ana Pérez');
  });

  it('si el login falla no guarda sesión', () => {
    crearServicio();
    service.login({ email: 'ana@uq.com', password: 'mala' }).subscribe({ error: () => {} });

    httpMock.expectOne(`${API_AUTH}/login`).flush(
      { message: 'Credenciales inválidas' }, { status: 401, statusText: 'Unauthorized' });

    expect(service.usuario()).toBeNull();
    expect(localStorage.getItem('uq-sesion')).toBeNull();
  });

  it('logout borra la sesión', () => {
    localStorage.setItem('uq-sesion', JSON.stringify(respuesta));
    crearServicio();

    service.logout();

    expect(service.usuario()).toBeNull();
    expect(service.token).toBeNull();
    expect(localStorage.getItem('uq-sesion')).toBeNull();
  });

  it('recupera la sesión guardada al iniciar (p. ej. al recargar la página)', () => {
    localStorage.setItem('uq-sesion', JSON.stringify(respuesta));
    crearServicio();
    expect(service.usuario()).toEqual(respuesta);
  });

  it('ignora una sesión guardada corrupta', () => {
    localStorage.setItem('uq-sesion', '{no es json');
    crearServicio();
    expect(service.usuario()).toBeNull();
  });

  describe('sesionActiva', () => {
    it('es false sin sesión', () => {
      crearServicio();
      expect(service.sesionActiva()).toBe(false);
    });

    it('es true con un token vigente', () => {
      localStorage.setItem('uq-sesion', JSON.stringify(respuesta));
      crearServicio();
      expect(service.sesionActiva()).toBe(true);
    });

    it('es false con un token vencido', () => {
      const vencido = { ...respuesta, token: tokenConExp(Math.floor(Date.now() / 1000) - 60) };
      localStorage.setItem('uq-sesion', JSON.stringify(vencido));
      crearServicio();
      expect(service.sesionActiva()).toBe(false);
    });

    it('es false con un token mal formado', () => {
      localStorage.setItem('uq-sesion', JSON.stringify({ ...respuesta, token: 'no-es-un-jwt' }));
      crearServicio();
      expect(service.sesionActiva()).toBe(false);
    });
  });
});
