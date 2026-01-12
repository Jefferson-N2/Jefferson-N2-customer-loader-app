import { Component, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil, switchMap, catchError, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { ClientService } from '../../services/client';
import { PaginatedResponse, ClientDetail } from '../../models';

/**
 * Componente que muestra tabla de clientes cargados
 * 
 * Características:
 * - Tabla con 6 columnas (Código, Nombre, ID, Email, Teléfono, Estado)
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
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
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
    'status'
  ];

  /** Tamaño de página actual */
  pageSize = 10;
  
  /** Opciones disponibles de tamaño de página */
  readonly pageSizeOptions = [5, 10, 25];
  
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
   * Maneja cambio de página en el paginador
   * 
   * @param event - Evento de cambio de página
   */
  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;

    const processId = this.processId$.value;
    if (processId) {
      this.loadClientsInternal(processId)
        .pipe(takeUntil(this.destroy$))
        .subscribe();
    }
  }

  /**
   * Función trackBy para optimizar renderizado de filas
   * 
   * @param index - Índice de la fila
   * @param item - Cliente a renderizar
   * @returns Identificador único del cliente
   * 
   * @example
   * ```html
   * <tr *ngFor="let client of clients; trackBy: trackByClientCode">
   * ```
   */
  trackByClientCode(_index: number, client: ClientDetail): string {
    return client.clientCode || '';
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
