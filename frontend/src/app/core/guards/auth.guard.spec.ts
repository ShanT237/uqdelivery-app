import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';

// Crea un JWT falso (sin firma válida) con el "exp" indicado, en segundos
function tokenConExp(exp: number): string {
  const payload = btoa(JSON.stringify({ sub: 'test@uq.com', exp }));
  return `header.${payload}.firma`;
}

function guardarSesion(token: string): void {
  localStorage.setItem('uq-sesion', JSON.stringify({
    token, email: 'test@uq.com', fullName: 'Test', role: 'USER'
  }));
}

function ejecutarGuard() {
  return TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));
}

describe('authGuard', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient()] });
  });

  it('redirige a /login si no hay sesión', () => {
    const resultado = ejecutarGuard();
    const router = TestBed.inject(Router);
    expect(resultado instanceof UrlTree).toBe(true);
    expect(router.serializeUrl(resultado as UrlTree)).toBe('/login');
  });

  it('deja pasar si hay un token vigente', () => {
    guardarSesion(tokenConExp(Math.floor(Date.now() / 1000) + 3600));
    expect(ejecutarGuard()).toBe(true);
  });

  it('redirige a /login y borra la sesión si el token está vencido', () => {
    guardarSesion(tokenConExp(Math.floor(Date.now() / 1000) - 60));
    const resultado = ejecutarGuard();
    expect(resultado instanceof UrlTree).toBe(true);
    expect(localStorage.getItem('uq-sesion')).toBeNull();
  });
});
