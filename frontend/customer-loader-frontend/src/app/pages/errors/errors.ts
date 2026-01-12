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
import { ErrorService } from '../../services/error-service';
import { PaginatedResponse, BulkLoadError } from '../../models';

/**
 * Componente que muestra tabla de errores en carga de datos
 * 
 * Características:
 * - Tabla con 5 columnas (Línea, Campo, Código, Mensaje, ID)
 * - Estados: cargando, vacío, error
 * - TrackBy para optimización de renderizado
 */
@Component({
  selector: 'app-errors',
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
  templateUrl: './errors.html',
  styleUrl: './errors.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Errors implements OnDestroy {
  /** ID del proceso de carga actual */
  readonly processId$ = new BehaviorSubject<string>('');
  
  /** Observable con datos paginados de errores */
  readonly errors$: Observable<PaginatedResponse<BulkLoadError>>;
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(false);
  
  /** Mensaje de error */
  readonly error$ = new BehaviorSubject<string | null>(null);

  /** Columnas a mostrar en la tabla */
  readonly displayedColumns: readonly string[] = [
    'lineNumber',
    'field',
    'code',
    'message',
    'id'
  ];

  /** Tamaño de página actual */
  pageSize = 5;
  
  /** Opciones disponibles de tamaño de página */
  readonly pageSizeOptions = [5, 10, 25];
  
  /** Índice de página actual (0-based) */
  currentPage = 0;

  private readonly destroy$ = new Subject<void>();
  private readonly errorsSubject$ = new BehaviorSubject<PaginatedResponse<BulkLoadError>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: this.pageSize,
    number: 0,
    empty: true
  });

  constructor(
    private readonly errorService: ErrorService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.errors$ = this.errorsSubject$.asObservable();
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
          return this.loadErrorsInternal(processId);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Carga los errores del servidor
   * 
   * @private
   */
  private loadErrorsInternal(processId: string): Observable<PaginatedResponse<BulkLoadError>> {
    this.isLoading$.next(true);
    this.error$.next(null);

    return this.errorService
      .getErrors(processId, this.currentPage, this.pageSize)
      .pipe(
        tap(data => {
          if (this.currentPage === 0) {
            this.errorsSubject$.next(data);
          } else {
            const current = this.errorsSubject$.value;
            this.errorsSubject$.next({
              ...data,
              content: [...current.content, ...data.content]
            });
          }
          this.isLoading$.next(false);
        }),
        catchError(error => {
          this.isLoading$.next(false);
          const errorMsg = error?.message || 'Error al cargar los errores';
          this.error$.next(`${errorMsg}`);
          return of(this.createEmptyResponse());
        })
      );
  }

  /**
   * Carga más errores
   */
  loadMore(): void {
    this.currentPage++;
    const processId = this.processId$.value;
    if (processId) {
      this.loadErrorsInternal(processId)
        .pipe(takeUntil(this.destroy$))
        .subscribe();
    }
  }

  /**
   * Verifica si hay más errores para cargar
   */
  canLoadMore(errors: PaginatedResponse<BulkLoadError>): boolean {
    if (!errors || !errors.content) return false;
    return errors.content.length < errors.totalElements;
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
      this.loadErrorsInternal(processId)
        .pipe(takeUntil(this.destroy$))
        .subscribe();
    }
  }

  /**
   * Función trackBy para optimizar renderizado de filas
   * 
   * @param index - Índice de la fila
   * @param item - Error a renderizar
   * @returns Identificador único del error
   */
  trackByLineNumber(_index: number, item: BulkLoadError): number {
    return item.lineNumber || _index;
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
  private createEmptyResponse(): PaginatedResponse<BulkLoadError> {
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
