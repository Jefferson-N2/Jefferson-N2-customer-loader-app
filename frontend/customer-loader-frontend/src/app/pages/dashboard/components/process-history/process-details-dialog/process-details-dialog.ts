import { Component, Inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BehaviorSubject } from 'rxjs';
import { ProcessService } from '../../../../../services/process';
import { ProcessDetails } from '../../../../../models';

/**
 * Componente de diálogo para mostrar detalles del procesamiento
 * 
 * Muestra:
 * - Información del proceso
 * - Estadísticas (exitosos, errores)
 * - Clientes procesados
 * - Errores encontrados
 */
@Component({
  selector: 'app-process-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './process-details-dialog.html',
  styleUrl: './process-details-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProcessDetailsDialogComponent implements OnInit {
  /** Datos del proceso */
  readonly processDetails$ = new BehaviorSubject<ProcessDetails | null>(null);
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(true);
  
  /** Mensaje de error */
  readonly error$ = new BehaviorSubject<string | null>(null);

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: { processId: string },
    private readonly processService: ProcessService
  ) {}

  ngOnInit(): void {
    this.loadProcessDetails();
  }

  /**
   * Carga los detalles del proceso
   * 
   * @private
   */
  private loadProcessDetails(): void {
    this.isLoading$.next(true);
    this.error$.next(null);

    this.processService.getProcessDetails(this.data.processId).subscribe({
      next: (details) => {
        this.processDetails$.next(details);
        this.isLoading$.next(false);
      },
      error: (error) => {
        this.isLoading$.next(false);
        const errorMsg = error?.message || 'Error al cargar detalles del proceso';
        this.error$.next(errorMsg);
      }
    });
  }

  /**
   * Calcula el porcentaje de éxito
   * 
   * @param details - Detalles del proceso
   */
  getSuccessPercentage(details: ProcessDetails | null): number {
    if (!details || details.totalRecords === 0) return 0;
    return Math.round((details.successfulCount / details.totalRecords) * 100);
  }

  /**
   * Formatea fecha para mostrar
   * 
   * @param date - Fecha a formatear
   */
  formatDate(date: string | Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }
}
