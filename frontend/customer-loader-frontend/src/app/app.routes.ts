import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.Dashboard),
    data: { title: 'Dashboard - Carga de Clientes' }
  },
  {
    path: 'clients/:processId',
    loadComponent: () => import('./pages/clients/clients').then(m => m.Clients),
    data: { title: 'Clientes' }
  },
  {
    path: 'errors/:processId',
    loadComponent: () => import('./pages/errors/errors').then(m => m.Errors),
    data: { title: 'Errores de Carga' }
  },
  {
    path: 'health',
    loadComponent: () => import('./pages/health/health').then(m => m.Health),
    data: { title: 'Estado de Salud' }
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
