import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { vi } from 'vitest';

import { Login } from './login';

const API_AUTH = 'http://localhost:8080/api/v1/auth';
const respuesta = { token: 'jwt', email: 'ana@uq.com', fullName: 'Ana Pérez', role: 'USER' };

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let httpMock: HttpTestingController;
  let navigate: ReturnType<typeof vi.spyOn>;

  async function crear(queryParams: Record<string, string> = {}): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { queryParamMap: convertToParamMap(queryParams) } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    await fixture.whenStable();
  }

  function texto(): string {
    fixture.detectChanges();
    return (fixture.nativeElement as HTMLElement).textContent ?? '';
  }

  beforeEach(() => localStorage.clear());
  afterEach(() => httpMock.verify());

  it('should create', async () => {
    await crear();
    expect(component).toBeTruthy();
    expect(component.modo()).toBe('login');
  });

  it('cambia entre las pestañas de login y registro', async () => {
    await crear();
    component.cambiarModo('registro');
    expect(texto()).toContain('Crea tu cuenta');
    component.cambiarModo('login');
    expect(texto()).toContain('Bienvenido');
  });

  describe('iniciar sesión', () => {
    it('con el formulario vacío no llama al servidor y muestra los errores', async () => {
      await crear();
      component.iniciarSesion();

      httpMock.expectNone(`${API_AUTH}/login`);
      expect(component.mensajeError('login', 'email')).toBe('Este campo es obligatorio');
      expect(component.mensajeError('login', 'password')).toBe('Este campo es obligatorio');
    });

    it('rechaza un correo con formato inválido', async () => {
      await crear();
      const email = component.loginForm.controls.email;
      email.setValue('no-es-correo');
      email.markAsTouched();
      expect(component.mensajeError('login', 'email')).toBe('Correo electrónico no válido');
    });

    it('con credenciales correctas guarda la sesión y lleva a /envios/nuevo', async () => {
      await crear();
      component.loginForm.setValue({ email: 'ana@uq.com', password: 'clave12345' });
      component.iniciarSesion();
      expect(component.enviando()).toBe(true);

      httpMock.expectOne(`${API_AUTH}/login`).flush(respuesta);

      expect(component.enviando()).toBe(false);
      expect(localStorage.getItem('uq-sesion')).not.toBeNull();
      expect(navigate).toHaveBeenCalledWith(['/envios/nuevo']);
    });

    it('con credenciales incorrectas muestra el mensaje del backend', async () => {
      await crear();
      component.loginForm.setValue({ email: 'ana@uq.com', password: 'mala' });
      component.iniciarSesion();

      httpMock.expectOne(`${API_AUTH}/login`).flush(
        { message: 'Credenciales inválidas', status: 401 }, { status: 401, statusText: 'Unauthorized' });

      expect(component.errorServidor()).toBe('Credenciales inválidas');
      expect(texto()).toContain('Credenciales inválidas');
      expect(navigate).not.toHaveBeenCalled();
    });

    it('si el backend no responde avisa que no hay conexión', async () => {
      await crear();
      component.loginForm.setValue({ email: 'ana@uq.com', password: 'clave12345' });
      component.iniciarSesion();

      httpMock.expectOne(`${API_AUTH}/login`).error(new ProgressEvent('error'), { status: 0 });

      expect(component.errorServidor()).toContain('No se pudo conectar con el servidor');
    });
  });

  describe('registrarse', () => {
    it('exige una contraseña de al menos 8 caracteres', async () => {
      await crear();
      const password = component.registroForm.controls.password;
      password.setValue('123');
      password.markAsTouched();
      expect(component.mensajeError('registro', 'password')).toBe('Debe tener al menos 8 caracteres');

      component.registrarse();
      httpMock.expectNone(`${API_AUTH}/register`);
    });

    it('rechaza un nombre o contraseña de solo espacios sin llamar al servidor', async () => {
      await crear();
      component.registroForm.setValue({ fullName: '     ', email: 'ana@uq.com', password: '          ' });
      component.registrarse();

      httpMock.expectNone(`${API_AUTH}/register`);
      expect(component.mensajeError('registro', 'fullName')).toBe('Este campo es obligatorio');
      expect(component.mensajeError('registro', 'password')).toBe('Este campo es obligatorio');
    });

    it('con datos válidos crea la cuenta y lleva a /envios/nuevo', async () => {
      await crear();
      component.registroForm.setValue({ fullName: 'Ana Pérez', email: 'ana@uq.com', password: 'clave12345' });
      component.registrarse();

      httpMock.expectOne(`${API_AUTH}/register`).flush(respuesta);

      expect(navigate).toHaveBeenCalledWith(['/envios/nuevo']);
    });

    it('si el correo ya existe muestra el mensaje del backend (409)', async () => {
      await crear();
      component.registroForm.setValue({ fullName: 'Ana Pérez', email: 'ana@uq.com', password: 'clave12345' });
      component.registrarse();

      httpMock.expectOne(`${API_AUTH}/register`).flush(
        { message: 'Ya existe un usuario registrado con el email: ana@uq.com' },
        { status: 409, statusText: 'Conflict' });

      expect(component.errorServidor()).toContain('Ya existe un usuario registrado');
    });

    it('muestra los errores por campo que devuelve el backend (400)', async () => {
      await crear();
      component.registroForm.setValue({ fullName: 'Ana Pérez', email: 'ana@uq.com', password: 'clave12345' });
      component.registrarse();

      httpMock.expectOne(`${API_AUTH}/register`).flush(
        { message: 'Datos de entrada inválidos', errors: { password: 'La contraseña debe tener al menos 8 caracteres' } },
        { status: 400, statusText: 'Bad Request' });

      expect(component.errorServidor()).toBe('La contraseña debe tener al menos 8 caracteres');
    });
  });

  it('muestra el aviso cuando llega con ?sesion=expirada', async () => {
    await crear({ sesion: 'expirada' });
    expect(component.sesionExpirada()).toBe(true);
    expect(texto()).toContain('Tu sesión expiró');
  });

  it('no muestra el aviso de sesión expirada en un ingreso normal', async () => {
    await crear();
    expect(texto()).not.toContain('Tu sesión expiró');
  });
});
