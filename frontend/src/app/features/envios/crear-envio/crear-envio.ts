import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EnvioService } from '../../../core/services/envio.service';
import { CrearEnvioRequest } from '../../../core/models/envio.model';

@Component({
  selector: 'app-crear-envio',
  imports: [ReactiveFormsModule],
  templateUrl: './crear-envio.html',
  styleUrl: './crear-envio.css'
})
export class CrearEnvio {
  private fb = inject(NonNullableFormBuilder);
  private envioService = inject(EnvioService);

  enviando = signal(false);
  errorServidor = signal<string | null>(null);
  codigoSeguimiento = signal<string | null>(null);

  // La estructura del formulario y sus reglas de validación
  form = this.fb.group({
    paquete: this.fb.group({
      descripcion: ['', [Validators.required, Validators.maxLength(200)]],
      pesoKg: this.fb.control<number | null>(null,
        [Validators.required, Validators.min(0.1), Validators.max(50)])
    }),
    origen: this.fb.group({
      calle: ['', Validators.required],
      ciudad: ['', Validators.required],
      referencia: ['']
    }),
    destino: this.fb.group({
      calle: ['', Validators.required],
      ciudad: ['', Validators.required],
      referencia: ['']
    }),
    receptor: this.fb.group({
      nombre: ['', Validators.required],
      telefono: ['', [Validators.required, Validators.pattern(/^\d{7,10}$/)]],
      email: ['', Validators.email]
    })
  });

  // Devuelve el mensaje de error de un campo ('' si está bien o no se ha tocado)
  mensajeError(ruta: string): string {
    const c = this.form.get(ruta);
    if (!c || !c.errors || !(c.touched || c.dirty)) return '';
    if (c.hasError('required')) return 'Este campo es obligatorio';
    if (c.hasError('email')) return 'Correo electrónico no válido';
    if (c.hasError('pattern')) return 'Formato no válido (solo números, 7 a 10 dígitos)';
    if (c.hasError('min')) return 'El valor es demasiado bajo';
    if (c.hasError('max')) return 'El valor es demasiado alto';
    if (c.hasError('maxlength')) return 'Texto demasiado largo';
    return 'Valor no válido';
  }

  enviar(): void {
    // Criterio 2: si hay errores, se muestran y NO se llama al servidor
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.errorServidor.set(null);
    const datos = this.form.getRawValue() as CrearEnvioRequest;

    // Criterio 1: crear el envío y mostrar el código de seguimiento
    this.envioService.crear(datos).subscribe({
      next: (resp) => {
        this.codigoSeguimiento.set(resp.codigoSeguimiento);
        this.enviando.set(false);
        this.form.reset();
      },
      error: (err) => {
        this.errorServidor.set(err.error?.message ?? 'No se pudo crear el envío');
        this.enviando.set(false);
      }
    });
  }
}