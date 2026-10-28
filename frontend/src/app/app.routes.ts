import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'envios/nuevo',
    loadComponent: () =>
      import('./features/envios/crear-envio/crear-envio').then(m => m.CrearEnvio)
  },
  { path: '', redirectTo: 'envios/nuevo', pathMatch: 'full' }
];