import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegistroRequest } from '../models/auth.model';
import { API_URL } from '../config/api.config';

const CLAVE_SESION = 'uq-sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${API_URL}/auth`;

  // Usuario con sesión activa (null si no ha iniciado sesión)
  usuario = signal<AuthResponse | null>(this.leerSesion());

  login(datos: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, datos)
      .pipe(tap(resp => this.guardarSesion(resp)));
  }

  registrar(datos: RegistroRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, datos)
      .pipe(tap(resp => this.guardarSesion(resp)));
  }

  logout(): void {
    localStorage.removeItem(CLAVE_SESION);
    this.usuario.set(null);
  }

  get token(): string | null {
    return this.usuario()?.token ?? null;
  }

  // true si hay token y aún no ha vencido (el backend lo emite con "exp" en segundos)
  sesionActiva(): boolean {
    const token = this.token;
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return typeof payload.exp !== 'number' || payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  private guardarSesion(resp: AuthResponse): void {
    localStorage.setItem(CLAVE_SESION, JSON.stringify(resp));
    this.usuario.set(resp);
  }

  private leerSesion(): AuthResponse | null {
    try {
      const guardada = localStorage.getItem(CLAVE_SESION);
      return guardada ? JSON.parse(guardada) : null;
    } catch {
      return null;
    }
  }
}
