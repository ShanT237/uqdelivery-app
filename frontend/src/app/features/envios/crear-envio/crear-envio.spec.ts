import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';
import { vi } from 'vitest';

import { CrearEnvio } from './crear-envio';

const API_ENVIOS = 'http://localhost:8080/api/v1/envios';

describe('CrearEnvio', () => {
  let component: CrearEnvio;
  let fixture: ComponentFixture<CrearEnvio>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [CrearEnvio],
      // EnvioService inyecta HttpClient; con HttpTestingController ninguna petición sale de verdad
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(CrearEnvio);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
  });

  afterEach(() => httpMock.verify());

  function llenarFormularioValido(): void {
    component.form.setValue({
      paquete: { descripcion: 'Libros', pesoKg: 2 },
      origen: { calle: 'Calle 1 # 2-3', ciudad: 'Armenia', referencia: '' },
      destino: { calle: 'Carrera 4 # 5-6', ciudad: 'Pereira', referencia: 'Casa azul' },
      receptor: { nombre: 'Ana', telefono: '3001234567', email: '' }
    });
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('con el formulario inválido no llama al servidor y marca los campos', () => {
    component.enviar();

    httpMock.expectNone(API_ENVIOS);
    expect(component.form.touched).toBe(true);
    expect(component.mensajeError('paquete.descripcion')).toBe('Este campo es obligatorio');
  });

  it('rechaza campos obligatorios de solo espacios sin llamar al servidor', () => {
    llenarFormularioValido();
    component.form.get('receptor.nombre')!.setValue('   ');
    component.form.get('destino.ciudad')!.setValue('   ');
    component.enviar();

    httpMock.expectNone(API_ENVIOS);
    expect(component.mensajeError('receptor.nombre')).toBe('Este campo es obligatorio');
    expect(component.mensajeError('destino.ciudad')).toBe('Este campo es obligatorio');
  });

  it('valida el formato del teléfono del receptor', () => {
    const telefono = component.form.get('receptor.telefono')!;
    telefono.setValue('12ab');
    telefono.markAsTouched();
    expect(component.mensajeError('receptor.telefono')).toContain('7 a 10 dígitos');
  });

  it('con el formulario válido crea el envío y muestra el código de seguimiento', () => {
    llenarFormularioValido();
    component.enviar();

    const req = httpMock.expectOne(API_ENVIOS);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.receptor.nombre).toBe('Ana');
    req.flush({ id: '1', codigoSeguimiento: 'UQ-123', estado: 'CREADO' });

    expect(component.codigoSeguimiento()).toBe('UQ-123');
    expect(component.enviando()).toBe(false);
  });

  it('si el servidor falla muestra el mensaje de error', () => {
    llenarFormularioValido();
    component.enviar();

    httpMock.expectOne(API_ENVIOS).flush(
      { message: 'Datos de entrada inválidos' }, { status: 400, statusText: 'Bad Request' });

    expect(component.errorServidor()).toBe('Datos de entrada inválidos');
    expect(component.enviando()).toBe(false);
  });

  it('el botón "Cerrar sesión" borra la sesión y lleva a /login', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    localStorage.setItem('uq-sesion', JSON.stringify({ token: 't', email: 'a@b.co', fullName: 'Test', role: 'USER' }));

    const boton: HTMLButtonElement = fixture.nativeElement.querySelector('.cerrar-sesion');
    boton.click();

    expect(localStorage.getItem('uq-sesion')).toBeNull();
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });
});
