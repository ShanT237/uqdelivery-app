import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegistroRequest } from '../models/auth.model';

const CLAVE_SESION = 'uq-sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/auth';

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
