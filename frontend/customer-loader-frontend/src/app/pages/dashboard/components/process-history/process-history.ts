import { Component, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil, tap, catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { ProcessService } from '../../../../services/process';
import { ClientService } from '../../../../services/client';
import { PaginatedResponse, BulkLoadProcess } from '../../../../models';
import { ProcessDetailsDialogComponent } from './process-details-dialog/process-details-dialog';
import { ClientDetailsDialogComponent } from './client-details-dialog/client-details-dialog';

/**
 * Componente que muestra el historial de procesos de carga
 * 
 * Características:
 * - Tabla con datos de procesos (ID, Estado, Registros, Fecha)
 * - Paginación
 * - Filtros por estado y nombre de archivo
 * - Modal para ver detalles del proceso
 * - Estados: cargando, vacío, error
 */
@Component({
  selector: 'app-process-history',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './process-history.html',
  styleUrl: './process-history.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProcessHistoryComponent implements OnDestroy {
  /** Observable con datos paginados de procesos */
  readonly processes$: Observable<PaginatedResponse<BulkLoadProcess>>;
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(false);
  
  /** Mensaje de error */
  readonly error$ = new BehaviorSubject<string | null>(null);

  /** Filtro por estado */
  readonly filterStatus$ = new BehaviorSubject<string>('');
  
  /** Filtro por nombre de archivo */
  readonly filterFileName$ = new BehaviorSubject<string>('');

  /** Columnas a mostrar en la tabla */
  readonly displayedColumns: readonly string[] = [
    'processId',
    'fileName',
    'totalRecords',
    'successfulCount',
    'errorCount',
    'processingDate',
    'status',
    'acciones'
  ];

  /** Opciones de estado para el filtro */
  readonly statusOptions = [
    { value: '', label: 'Todos los estados' },
    { value: 'COMPLETED', label: 'Completado' },
    { value: 'PROCESSING', label: 'Procesando' },
    { value: 'FAILED', label: 'Fallido' },
    { value: 'PENDING', label: 'Pendiente' }
  ];

  /** Tamaño de página actual */
  pageSize = 10;
  
  /** Opciones disponibles de tamaño de página */
  readonly pageSizeOptions = [5, 10, 25];
  
  /** Índice de página actual (0-based) */
  currentPage = 0;

  private readonly destroy$ = new Subject<void>();
  private readonly processesSubject$ = new BehaviorSubject<PaginatedResponse<BulkLoadProcess>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: this.pageSize,
    number: 0,
    empty: true
  });

  constructor(
    private readonly processService: ProcessService,
    private readonly clientService: ClientService,
    private readonly router: Router,
    private readonly dialog: MatDialog,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.processes$ = this.processesSubject$.asObservable();
    
    // Detectar cambios en filtros
    this.filterStatus$
      .pipe(
        tap(() => {
          this.currentPage = 0;
          this.loadProcesses();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();

    this.filterFileName$
      .pipe(
        tap(() => {
          this.currentPage = 0;
          this.loadProcesses();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();

    this.loadProcesses();
  }

  /**
   * Carga los procesos del servidor
   * 
   * @private
   */
  private loadProcesses(): void {
    this.isLoading$.next(true);
    this.error$.next(null);

    this.processService
      .getAllProcesses(this.currentPage, this.pageSize)
      .pipe(
        tap(data => {
          // Aplicar filtros locales si es necesario
          const filteredData = this.applyLocalFilters(data);
          this.processesSubject$.next(filteredData);
          this.isLoading$.next(false);
          this.cdr.markForCheck();
        }),
        catchError(error => {
          this.isLoading$.next(false);
          const errorMsg = error?.message || 'Error al cargar procesos';
          this.error$.next(errorMsg);
          this.cdr.markForCheck();
          return of(this.createEmptyResponse());
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Aplica filtros locales a los datos de procesos
   * 
   * @param data - Datos paginados originales
   * @private
   */
  private applyLocalFilters(data: PaginatedResponse<BulkLoadProcess>): PaginatedResponse<BulkLoadProcess> {
    let filtered = [...data.content];
    const statusFilter = this.filterStatus$.value?.toUpperCase() || '';
    const fileNameFilter = this.filterFileName$.value?.toLowerCase() || '';

    // Filtro por estado
    if (statusFilter) {
      filtered = filtered.filter(p => p.status?.toUpperCase() === statusFilter);
    }

    // Filtro por nombre de archivo
    if (fileNameFilter) {
      filtered = filtered.filter(p => 
        p.fileName?.toLowerCase().includes(fileNameFilter)
      );
    }

    return {
      ...data,
      content: filtered,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / this.pageSize),
      empty: filtered.length === 0
    };
  }

  /**
   * Limpia los filtros
   */
  clearFilters(): void {
    this.filterStatus$.next('');
    this.filterFileName$.next('');
    this.currentPage = 0;
    this.loadProcesses();
  }

  /**
   * Recarga los procesos desde el servidor
   */
  reloadProcesses(): void {
    this.currentPage = 0;
    this.loadProcesses();
  }

  /**
   * Maneja cambio de página en el paginador
   * 
   * @param event - Evento de cambio de página
   */
  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadProcesses();
  }

  /**
   * Abre modal con detalles del proceso
   * 
   * @param processId - ID del proceso a mostrar
   */
  viewDetails(processId: string): void {
    this.dialog.open(ProcessDetailsDialogComponent, {
      width: '90%',
      maxWidth: '900px',
      data: { processId }
    });
  }

  /**
   * Abre modal con clientes del proceso
   * 
   * @param processId - ID del proceso
   */
  viewClients(processId: string): void {
    // Obtener el primer cliente para mostrar en el modal
    this.clientService
      .getClients(processId, 0, 1)
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (response) => {
          if (response.content && response.content.length > 0) {
            this.dialog.open(ClientDetailsDialogComponent, {
              width: '95%',
              maxWidth: '1000px',
              data: { clientDetail: response.content[0] }
            });
          }
        },
        error: () => {
          // Si falla, navega a la página de clientes
          this.router.navigate(['/clients', processId]);
        }
      });
  }

  /**
   * Navega a errores del proceso
   * 
   * @param processId - ID del proceso
   */
  viewErrors(processId: string): void {
    this.router.navigate(['/errors', processId]);
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
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Función trackBy para optimizar renderizado de filas
   * 
   * @param index - Índice de la fila
   * @param item - Proceso a renderizar
   */
  trackByProcessId(_index: number, item: BulkLoadProcess): string {
    return item.processId || '';
  }

  /**
   * Crea una respuesta paginada vacía
   * 
   * @private
   */
  private createEmptyResponse(): PaginatedResponse<BulkLoadProcess> {
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
