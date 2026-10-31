import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';

import { CrearEnvio } from './crear-envio';

describe('CrearEnvio', () => {
  let component: CrearEnvio;
  let fixture: ComponentFixture<CrearEnvio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrearEnvio],
    }).compileComponents();

    fixture = TestBed.createComponent(CrearEnvio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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
