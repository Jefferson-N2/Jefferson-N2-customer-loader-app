import { Component, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil, switchMap, catchError, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { ClientService } from '../../services/client';
import { PaginatedResponse, ClientDetail } from '../../models';

/**
 * Componente que muestra tabla de clientes cargados
 * 
 * Características:
 * - Tabla con 8 columnas (Código, Nombre, ID, Email, Teléfono, Estado Cuenta, Número Cuenta, Nómina)
 * - Paginación (5, 10, 25 registros)
 * - Estados: cargando, vacío, error
 * - TrackBy para optimización de renderizado
 */
@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatTooltipModule
  ],
  templateUrl: './clients.html',
  styleUrl: './clients.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Clients implements OnDestroy {
  /** ID del proceso de carga actual */
  readonly processId$ = new BehaviorSubject<string>('');
  
  /** Observable con datos paginados de clientes */
  readonly clients$: Observable<PaginatedResponse<ClientDetail>>;
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(false);
  
  /** Mensaje de error */
  readonly error$ = new BehaviorSubject<string | null>(null);

  /** Columnas a mostrar en la tabla */
  readonly displayedColumns: readonly string[] = [
    'clientCode',
    'name',
    'id',
    'email',
    'phone',
    'status',
    'accountNumber',
    'payrollValue'
  ];

  /** Tamaño de página actual */
  pageSize = 10;
  
  /** Opciones disponibles de tamaño de página */
  readonly pageSizeOptions = [5, 10, 25, 50];
  
  /** Índice de página actual (0-based) */
  currentPage = 0;

  private readonly destroy$ = new Subject<void>();
  private readonly clientsSubject$ = new BehaviorSubject<PaginatedResponse<ClientDetail>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: this.pageSize,
    number: 0,
    empty: true
  });

  constructor(
    private readonly clientService: ClientService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.clients$ = this.clientsSubject$.asObservable();
    this.initializeComponent();
  }

  /**
   * Inicializa el componente y carga los datos
   * 
   * @private
   */
  private initializeComponent(): void {
    this.route.params
      .pipe(
        tap(params => {
          const processId = params['processId'];
          if (!processId) {
            this.error$.next('ID de proceso no proporcionado');
          }
          this.processId$.next(processId);
        }),
        switchMap(params => {
          const processId = params['processId'];
          if (!processId) {
            return of(this.createEmptyResponse());
          }
          return this.loadClientsInternal(processId);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Carga los clientes del servidor
   * Reemplaza completamente el contenido (sin concatenación)
   * 
   * @private
   */
  private loadClientsInternal(processId: string): Observable<PaginatedResponse<ClientDetail>> {
    this.isLoading$.next(true);
    this.error$.next(null);

    return this.clientService
      .getClients(processId, this.currentPage, this.pageSize)
      .pipe(
        tap(data => {
          this.clientsSubject$.next(data);
          this.isLoading$.next(false);
        }),
        catchError(error => {
          this.isLoading$.next(false);
          const errorMsg = error?.message || 'Error al cargar los clientes';
          this.error$.next(`${errorMsg}`);
          return of(this.createEmptyResponse());
        })
      );
  }

  /**
   * Refresca los datos de la tabla
   */
  refreshClients(): void {
    const processId = this.processId$.value;
    if (processId) {
      this.currentPage = 0;
      this.loadClientsInternal(processId)
        .pipe(takeUntil(this.destroy$))
        .subscribe();
    }
  }

  /**
   * Maneja el cambio de página del paginador
   */
  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    const processId = this.processId$.value;
    if (processId) {
      this.loadClientsInternal(processId)
        .pipe(takeUntil(this.destroy$))
        .subscribe();
    }
  }

  /**
   * Vuelve atrás al dashboard
   */
  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  /**
   * Crea una respuesta paginada vacía
   * 
   * @private
   */
  private createEmptyResponse(): PaginatedResponse<ClientDetail> {
    return {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: this.pageSize,
      number: 0,
      empty: true
    };
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
