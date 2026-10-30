import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

type Modo = 'login' | 'registro';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private fb = inject(NonNullableFormBuilder);

  modo = signal<Modo>('login');
  verPassword = signal(false);

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

  // Por ahora solo valida; la conexión con /api/v1/auth se hace después
  iniciarSesion(): void {
    this.loginForm.markAllAsTouched();
  }

  registrarse(): void {
    this.registroForm.markAllAsTouched();
  }
}
