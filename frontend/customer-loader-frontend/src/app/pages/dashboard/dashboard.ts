import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { BulkLoadComponent } from './components/bulk-load/bulk-load';
import { ProcessHistoryComponent } from './components/process-history/process-history';

/**
 * Componente contenedor del dashboard
 * 
 * Actúa como layout principal con:
 * - Componente de carga masiva
 * - Historial de procesos
 * - Router outlet para componentes hijo
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    BulkLoadComponent,
    ProcessHistoryComponent
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Dashboard {}
