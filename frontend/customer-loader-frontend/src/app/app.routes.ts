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
    data: { title: 'Dashboard - Carga Masiva de Clientes' },
    children: [
      {
        path: 'clients/:processId',
        loadComponent: () => import('./pages/clients/clients').then(m => m.Clients),
        data: { title: 'Clientes Cargados' }
      },
      {
        path: 'errors/:processId',
        loadComponent: () => import('./pages/errors/errors').then(m => m.Errors),
        data: { title: 'Errores de Carga' }
      }
    ]
  },
  {
    path: 'clientes',
    loadComponent: () => import('./pages/dashboard/components/all-clients/all-clients').then(m => m.AllClientsComponent),
    data: { title: 'Todos los Clientes' }
  },
  {
    path: 'health',
    loadComponent: () => import('./pages/health/health').then(m => m.Health),
    data: { title: 'Estado de Salud del Sistema' }
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
