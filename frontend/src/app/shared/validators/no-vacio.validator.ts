import { AbstractControl, ValidationErrors } from '@angular/forms';

// Como Validators.required, pero también rechaza textos de solo espacios ("   "),
// igual que @NotBlank en el backend. Devuelve la misma clave 'required' para reutilizar
// el mensaje "Este campo es obligatorio" de los formularios.
export function noVacio(control: AbstractControl): ValidationErrors | null {
  const valor = control.value;
  if (valor === null || valor === undefined) return { required: true };
  if (typeof valor === 'string' && valor.trim() === '') return { required: true };
  return null;
}
