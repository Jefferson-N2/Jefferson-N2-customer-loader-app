import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Observable, Subject, timer, BehaviorSubject } from 'rxjs';
import { switchMap, takeUntil, map, tap } from 'rxjs/operators';
import { HealthService } from '../../services/health';
import { HealthResponse } from '../../models';
import { HealthStatus } from '../../shared/constants';

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
})
export class Health implements OnInit, OnDestroy {
  health$: Observable<HealthResponse>;
  isLoading$ = new BehaviorSubject<boolean>(false);
  lastRefresh$ = new BehaviorSubject<Date>(new Date());
  
  private destroy$ = new Subject<void>();
  private refreshInterval = 20000; // 10 segundos

  HealthStatus = HealthStatus;

  constructor(private healthService: HealthService) {
    this.health$ = this.createHealthObservable();
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Crea un observable que se auto-refresca cada 10 segundos
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
   * Refresca el estado manualmente
   */
  refreshHealth(): void {
    this.isLoading$.next(true);
    this.healthService
      .getHealth()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (health) => {
          this.lastRefresh$.next(new Date());
          this.isLoading$.next(false);
        },
        error: () => this.isLoading$.next(false),
      });
  }

  /**
   * Retorna la clase CSS según el estado
   */
  getStatusClass(status: string | undefined): string {
    return status === HealthStatus.UP ? 'status-up' : 'status-down';
  }

  /**
   * Retorna el ícono según el estado
   */
  getStatusIcon(status: string | undefined): string {
    return status === HealthStatus.UP ? 'check_circle' : 'error_circle';
  }
}
