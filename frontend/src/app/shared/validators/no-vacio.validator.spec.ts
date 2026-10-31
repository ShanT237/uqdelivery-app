import { FormControl } from '@angular/forms';
import { noVacio } from './no-vacio.validator';

describe('noVacio', () => {
  it('rechaza un texto vacío', () => {
    expect(noVacio(new FormControl(''))).toEqual({ required: true });
  });

  it('rechaza un texto de solo espacios (como @NotBlank del backend)', () => {
    expect(noVacio(new FormControl('    '))).toEqual({ required: true });
    expect(noVacio(new FormControl(' \t\n '))).toEqual({ required: true });
  });

  it('rechaza null', () => {
    expect(noVacio(new FormControl(null))).toEqual({ required: true });
  });

  it('acepta un texto con contenido, aunque tenga espacios alrededor', () => {
    expect(noVacio(new FormControl('Ana'))).toBeNull();
    expect(noVacio(new FormControl('  Ana Pérez  '))).toBeNull();
  });

  it('acepta valores que no son texto, como números', () => {
    expect(noVacio(new FormControl(0))).toBeNull();
  });
});
