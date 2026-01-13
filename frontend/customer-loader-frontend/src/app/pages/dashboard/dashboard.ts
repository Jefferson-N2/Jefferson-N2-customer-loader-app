import { Component, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
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
    MatButtonModule,
    BulkLoadComponent,
    ProcessHistoryComponent
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Dashboard {
  @ViewChild(ProcessHistoryComponent) processHistoryComponent?: ProcessHistoryComponent;

  constructor(private readonly router: Router) {}

  /**
   * Maneja el evento de carga exitosa
   * Refresca el historial de procesos
   */
  onUploadSuccess(response: any): void {
    if (this.processHistoryComponent) {
      this.processHistoryComponent.refreshProcesses();
    }
  }
}

