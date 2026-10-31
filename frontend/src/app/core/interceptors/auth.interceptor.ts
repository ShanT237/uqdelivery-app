import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { API_URL } from '../config/api.config';

// Solo actúa sobre peticiones a NUESTRA API: el token nunca se envía a servicios externos
// 1. Agrega "Authorization: Bearer <token>" si hay sesión activa
// 2. Si el backend responde 401/403 (token vencido o inválido), cierra la sesión y lleva a /login
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(`${API_URL}/`)) return next(req);

  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.token;
  const peticion = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(peticion).pipe(
    catchError((err: unknown) => {
      // En /auth un 401 significa "credenciales inválidas": ese error lo muestra el formulario de login
      const esRutaAuth = req.url.startsWith(`${API_URL}/auth/`);
      if (err instanceof HttpErrorResponse && (err.status === 401 || err.status === 403) && !esRutaAuth) {
        authService.logout();
        router.navigate(['/login'], { queryParams: { sesion: 'expirada' } });
      }
      return throwError(() => err);
    })
  );
};
