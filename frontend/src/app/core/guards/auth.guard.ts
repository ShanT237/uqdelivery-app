import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

// Solo deja entrar a la ruta si hay una sesión con token vigente; si no, redirige a /login
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  if (authService.sesionActiva()) return true;

  authService.logout();   // limpia una sesión vencida, si la había
  return inject(Router).createUrlTree(['/login']);
};
