import { Component, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Observable, Subject, timer, BehaviorSubject } from 'rxjs';
import { switchMap, takeUntil, tap } from 'rxjs/operators';
import { HealthService } from '../../services/health';
import { HealthResponse } from '../../models';
import { HealthStatus } from '../../shared/constants';

/**
 * Componente de monitoreo de salud del servidor
 * 
 * Características:
 * - Auto-refresco cada 10 segundos
 * - Indicadores de estado para backend, BD y servicios externos
 * - Refresco manual con botón
 * - Timestamps de última actualización
 */
@Component({
  selector: 'app-health',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './health.html',
  styleUrl: './health.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Health implements OnDestroy {
  /** Observable con estado de salud */
  readonly health$: Observable<HealthResponse>;
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(false);
  
  /** Timestamp de último refresco */
  readonly lastRefresh$ = new BehaviorSubject<Date>(new Date());
  
  /** Enum de estados disponibles */
  readonly HealthStatus = HealthStatus;

  private readonly destroy$ = new Subject<void>();
  private readonly refreshInterval = 10000; // 10 segundos

  constructor(private readonly healthService: HealthService) {
    this.health$ = this.createHealthObservable();
  }

  /**
   * Crea un observable que se auto-refresca cada 10 segundos
   * 
   * @private
   * @returns Observable con estado de salud del servidor
   */
  private createHealthObservable(): Observable<HealthResponse> {
    return timer(0, this.refreshInterval).pipe(
      tap(() => this.isLoading$.next(true)),
      switchMap(() => this.healthService.getHealth()),
      tap(() => {
        this.isLoading$.next(false);
        this.lastRefresh$.next(new Date());
      }),
      takeUntil(this.destroy$)
    );
  }

  /**
   * Refresca manualmente el estado de salud
   * 
   * @example
   * ```typescript
   * // En el template:
   * <button (click)="refreshHealth()">Refrescar</button>
   * ```
   */
  refreshHealth(): void {
    this.isLoading$.next(true);
    this.healthService
      .getHealth()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.lastRefresh$.next(new Date());
          this.isLoading$.next(false);
        },
        error: () => {
          this.isLoading$.next(false);
          console.error('[HealthComponent] Error al refrescar estado');
        }
      });
  }

  /**
   * Retorna la clase CSS según el estado
   * 
   * @param status - Estado del componente (UP/DOWN)
   * @returns Clase CSS a aplicar
   * 
   * @example
   * ```html
   * <div [ngClass]="getStatusClass(health.status)">
   * ```
   */
  getStatusClass(status: string | undefined): string {
    return status === HealthStatus.UP ? 'status-up' : 'status-down';
  }

  /**
   * Retorna el ícono según el estado
   * 
   * @param status - Estado del componente (UP/DOWN)
   * @returns Nombre del ícono Material
   */
  getStatusIcon(status: string | undefined): string {
    return status === HealthStatus.UP ? 'check_circle' : 'error_circle';
  }

  /**
   * Formatea timestamp del servidor
   */
  formatTimestamp(timestamp: string | undefined): string {
    if (!timestamp) return 'N/A';
    try {
      // Si el timestamp viene en formato ISO, usarlo directamente
      if (timestamp.includes('T') || timestamp.includes('-')) {
        return new Date(timestamp).toLocaleString('es-ES');
      }
      // Si viene en formato de array de números separados por comas
      if (timestamp.includes(',')) {
        const parts = timestamp.split(',').map(p => parseInt(p.trim()));
        if (parts.length >= 6) {
          // [year, month, day, hour, minute, second, nanoseconds]
          const date = new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5]);
          return date.toLocaleString('es-ES');
        }
      }
      return timestamp;
    } catch {
      return timestamp;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
