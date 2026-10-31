import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/auth.model';

type Modo = 'login' | 'registro';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private fb = inject(NonNullableFormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  modo = signal<Modo>('login');
  verPassword = signal(false);
  enviando = signal(false);
  errorServidor = signal<string | null>(null);

  // El interceptor redirige con ?sesion=expirada cuando el backend rechaza el token
  sesionExpirada = signal(
    inject(ActivatedRoute).snapshot.queryParamMap.get('sesion') === 'expirada'
  );

  // Mismas reglas que LoginRequestDto en el backend
  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  // Mismas reglas que RegisterRequestDto en el backend
  registroForm = this.fb.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  cambiarModo(modo: Modo): void {
    this.modo.set(modo);
    this.verPassword.set(false);
    this.errorServidor.set(null);
  }

  // Devuelve el mensaje de error de un campo ('' si está bien o no se ha tocado)
  mensajeError(form: 'login' | 'registro', campo: string): string {
    const c = form === 'login' ? this.loginForm.get(campo) : this.registroForm.get(campo);
    if (!c || !c.errors || !(c.touched || c.dirty)) return '';
    if (c.hasError('required')) return 'Este campo es obligatorio';
    if (c.hasError('email')) return 'Correo electrónico no válido';
    if (c.hasError('minlength')) return 'Debe tener al menos 8 caracteres';
    return 'Valor no válido';
  }

  iniciarSesion(): void {
    // Si hay errores, se muestran y NO se llama al servidor
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.enviar(this.authService.login(this.loginForm.getRawValue()));
  }

  registrarse(): void {
    if (this.registroForm.invalid) {
      this.registroForm.markAllAsTouched();
      return;
    }
    this.enviar(this.authService.registrar(this.registroForm.getRawValue()));
  }

  private enviar(peticion: Observable<AuthResponse>): void {
    this.enviando.set(true);
    this.errorServidor.set(null);
    this.sesionExpirada.set(false);

    peticion.subscribe({
      next: () => {
        this.enviando.set(false);
        this.router.navigate(['/envios/nuevo']);
      },
      error: (err: HttpErrorResponse) => {
        this.enviando.set(false);
        this.errorServidor.set(this.mensajeServidor(err));
      }
    });
  }

  // Traduce la respuesta de error del backend (GlobalExceptionHandler) a un mensaje
  private mensajeServidor(err: HttpErrorResponse): string {
    if (err.status === 0) return 'No se pudo conectar con el servidor. Intenta más tarde.';
    const errores = err.error?.errors as Record<string, string> | undefined;
    if (errores) return Object.values(errores).join('. ');
    return err.error?.message ?? 'Ocurrió un error inesperado';
  }
}
