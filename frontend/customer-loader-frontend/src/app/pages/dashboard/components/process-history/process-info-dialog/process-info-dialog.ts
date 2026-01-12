import { Component, Inject, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil, catchError, tap, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { of } from 'rxjs';
import { ProcessService } from '../../../../../services/process';
import { ClientService } from '../../../../../services/client';
import { ProcessDetails, BulkLoadError, ClientDetail, PaginatedResponse } from '../../../../../models';
import { ClientDetailsDialogComponent } from '../client-details-dialog/client-details-dialog';

/**
 * Modal consolidada que muestra:
 * - Información del proceso
 * - Tabla de errores (con filtros)
 * - Tabla de clientes (con filtros)
 */
@Component({
  selector: 'app-process-info-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule
  ],
  templateUrl: './process-info-dialog.html',
  styleUrl: './process-info-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProcessInfoDialogComponent implements OnInit, OnDestroy {
  // ==================== INFORMACIÓN DEL PROCESO ====================
  readonly processDetails$ = new BehaviorSubject<ProcessDetails | null>(null);
  
  // ==================== ERRORES ====================
  readonly errors$: Observable<PaginatedResponse<BulkLoadError>>;
  readonly errorFilterIdType$ = new BehaviorSubject<string>('');
  readonly errorCurrentPage$ = new BehaviorSubject<number>(0);
  readonly errorDisplayedColumns: readonly string[] = ['lineNumber', 'idType', 'idNumber', 'errorType', 'errorMessage'];
  
  // ==================== CLIENTES ====================
  readonly clients$: Observable<PaginatedResponse<ClientDetail>>;
  readonly clientFilterName$ = new BehaviorSubject<string>('');
  readonly clientFilterCode$ = new BehaviorSubject<string>('');
  readonly clientCurrentPage$ = new BehaviorSubject<number>(0);
  readonly clientDisplayedColumns: readonly string[] = ['clientCode', 'name', 'email', 'phone', 'accountStatus', 'actions'];
  
  // ==================== ESTADO GENERAL ====================
  readonly isLoading$ = new BehaviorSubject<boolean>(true);
  readonly error$ = new BehaviorSubject<string | null>(null);
  readonly isLoadingErrors$ = new BehaviorSubject<boolean>(false);
  readonly isLoadingClients$ = new BehaviorSubject<boolean>(false);

  private readonly destroy$ = new Subject<void>();
  private readonly processId: string;

  private readonly errorsSubject$ = new BehaviorSubject<PaginatedResponse<BulkLoadError>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 5,
    number: 0,
    empty: true
  });

  private readonly clientsSubject$ = new BehaviorSubject<PaginatedResponse<ClientDetail>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 5,
    number: 0,
    empty: true
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: { processId: string },
    private readonly processService: ProcessService,
    private readonly clientService: ClientService,
    private readonly dialog: MatDialog,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.processId = data.processId;
    this.errors$ = this.errorsSubject$.asObservable();
    this.clients$ = this.clientsSubject$.asObservable();
  }

  ngOnInit(): void {
    this.loadProcessDetails();
    
    this.errorFilterIdType$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.errorCurrentPage$.next(0);
          this.loadErrors();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();

    this.clientFilterName$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.clientCurrentPage$.next(0);
          this.loadClients();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();

    this.clientFilterCode$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.clientCurrentPage$.next(0);
          this.loadClients();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();

    this.loadErrors();
    this.loadClients();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Carga detalles del proceso
   */
  private loadProcessDetails(): void {
    this.processService
      .getAllProcesses(0, 100) // Obtener todos los procesos para encontrar el actual
      .pipe(
        tap(response => {
          // Buscar el proceso actual en la lista
          const process = (response as any).content?.find((p: any) => p.processId === this.processId);
          if (process) {
            this.processDetails$.next(process);
          }
          this.isLoading$.next(false);
          this.cdr.markForCheck();
        }),
        catchError(error => {
          this.error$.next(error?.message || 'Error al cargar detalles');
          this.isLoading$.next(false);
          this.cdr.markForCheck();
          return of(null);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Carga errores del proceso
   */
  private loadErrors(): void {
    this.isLoadingErrors$.next(true);
    
    // Usar el endpoint /processes/{processId}/errors que devuelve datos paginados
    this.processService
      .getProcessErrors(this.processId, this.errorCurrentPage$.value, 5)
      .pipe(
        tap((data: any) => {
          if (data) {
            // Manejar tanto respuesta paginada como array directo
            let errorsData = data.content || data;
            if (!Array.isArray(errorsData)) {
              errorsData = [];
            }
            
            // Aplicar solo filtro de tipo ID localmente
            const idTypeFilter = this.errorFilterIdType$.value;
            if (idTypeFilter) {
              errorsData = errorsData.filter((error: any) => 
                error.idType?.toUpperCase() === idTypeFilter.toUpperCase()
              );
            }
            
            const processedData = {
              content: errorsData,
              totalElements: data.totalElements || errorsData.length,
              totalPages: data.totalPages || Math.ceil(errorsData.length / 5),
              size: 5,
              number: this.errorCurrentPage$.value,
              empty: errorsData.length === 0
            };
            
            // Acumular datos si no es la primera página y no hay filtros
            if (this.errorCurrentPage$.value === 0 || idTypeFilter) {
              this.errorsSubject$.next(processedData);
            } else {
              const current = this.errorsSubject$.value;
              this.errorsSubject$.next({
                ...processedData,
                content: [...current.content, ...processedData.content]
              });
            }
          } else {
            this.errorsSubject$.next({
              content: [],
              totalElements: 0,
              totalPages: 0,
              size: 5,
              number: 0,
              empty: true
            });
          }
          this.isLoadingErrors$.next(false);
          this.cdr.markForCheck();
        }),
        catchError((error) => {
          console.error('Error loading errors:', error);
          this.isLoadingErrors$.next(false);
          this.errorsSubject$.next({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: 5,
            number: 0,
            empty: true
          });
          return of(null);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Carga clientes del proceso
   */
  private loadClients(): void {
    this.isLoadingClients$.next(true);
    
    this.clientService
      .getClients(this.processId, this.clientCurrentPage$.value, 5)
      .pipe(
        tap(data => {
          let clientsData = data.content || [];
          
          // Aplicar filtros localmente
          const nameFilter = this.clientFilterName$.value?.toLowerCase() || '';
          const codeFilter = this.clientFilterCode$.value?.toLowerCase() || '';
          
          if (nameFilter || codeFilter) {
            clientsData = clientsData.filter((client: any) => {
              const matchesName = !nameFilter || 
                `${client.firstNames} ${client.lastNames}`.toLowerCase().includes(nameFilter);
              const matchesCode = !codeFilter || 
                client.clientCode?.toLowerCase().includes(codeFilter);
              return matchesName && matchesCode;
            });
          }
          
          const processedData = {
            ...data,
            content: clientsData,
            totalElements: data.totalElements,
            empty: clientsData.length === 0
          };
          
          // Si hay filtros o es página 0, reemplazar; si no, acumular
          if (this.clientCurrentPage$.value === 0 || nameFilter || codeFilter) {
            this.clientsSubject$.next(processedData);
          } else {
            const current = this.clientsSubject$.value;
            this.clientsSubject$.next({
              ...processedData,
              content: [...current.content, ...processedData.content]
            });
          }
          
          this.isLoadingClients$.next(false);
          this.cdr.markForCheck();
        }),
        catchError((error) => {
          console.error('Error loading clients:', error);
          this.isLoadingClients$.next(false);
          this.clientsSubject$.next({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: 5,
            number: 0,
            empty: true
          });
          return of(null);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Aplica filtros locales a errores
   */
  private applyErrorFilters(data: PaginatedResponse<BulkLoadError>): PaginatedResponse<BulkLoadError> {
    let filtered = [...data.content];
    const idTypeFilter = this.errorFilterIdType$.value.toUpperCase();

    if (idTypeFilter) {
      filtered = filtered.filter(e => e.idType?.toUpperCase() === idTypeFilter);
    }

    return {
      ...data,
      content: filtered,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / 5),
      empty: filtered.length === 0
    };
  }

  /**
   * Aplica filtros locales a clientes
   */
  private applyClientFilters(data: PaginatedResponse<ClientDetail>): PaginatedResponse<ClientDetail> {
    let filtered = [...data.content];
    const nameFilter = this.clientFilterName$.value.toLowerCase();
    const codeFilter = this.clientFilterCode$.value.toLowerCase();

    if (nameFilter) {
      filtered = filtered.filter(c => {
        const fullName = `${c.firstNames} ${c.lastNames}`.toLowerCase();
        return fullName.includes(nameFilter);
      });
    }

    if (codeFilter) {
      filtered = filtered.filter(c => c.clientCode?.toLowerCase().includes(codeFilter));
    }

    return {
      ...data,
      content: filtered,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / 5),
      empty: filtered.length === 0
    };
  }

  /**
   * Carga más errores
   */
  loadMoreErrors(): void {
    this.errorCurrentPage$.next(this.errorCurrentPage$.value + 1);
    this.loadErrors();
  }

  /**
   * Carga más clientes
   */
  loadMoreClients(): void {
    this.clientCurrentPage$.next(this.clientCurrentPage$.value + 1);
    this.loadClients();
  }

  /**
   * Verifica si hay más errores para cargar
   */
  canLoadMoreErrors(errors: PaginatedResponse<BulkLoadError>): boolean {
    if (!errors || !errors.content) return false;
    // Si hay menos registros que el total elementos, hay más para cargar
    return errors.content.length < errors.totalElements;
  }

  /**
   * Verifica si hay más clientes para cargar
   */
  canLoadMoreClients(clients: PaginatedResponse<ClientDetail>): boolean {
    if (!clients || !clients.content) return false;
    // Si hay menos registros que el total elementos, hay más para cargar
    return clients.content.length < clients.totalElements;
  }

  /**
   * Obtiene etiqueta de tipo de ID
   */
  getIdTypeLabel(idType: string): string {
    return idType === 'C' ? 'Persona Natural' : 'Persona Jurídica';
  }

  /**
   * Obtiene el valor mínimo entre dos números
   */
  getMinValue(a: number, b: number): number {
    return Math.min(a, b);
  }

  /**
   * Abre modal con detalles del cliente
   */
  viewClientDetails(client: ClientDetail): void {
    this.dialog.open(ClientDetailsDialogComponent, {
      width: '95%',
      maxWidth: '1000px',
      data: { clientDetail: client }
    });
  }

  /**
   * Trackby para optimizar renderizado
   */
  trackByErrorId(_index: number, item: BulkLoadError): number | string | undefined {
    return item.id || item.lineNumber;
  }

  trackByClientCode(_index: number, item: ClientDetail): string {
    return item.clientCode || '';
  }

  /**
   * Calcula el porcentaje de éxito
   */
  getSuccessPercentage(): number {
    const processDetails = this.processDetails$.value;
    if (!processDetails) return 0;
    
    const total = processDetails.totalRecords || 0;
    const successful = processDetails.successfulCount || 0;
    
    if (total === 0) return 0;
    return Math.round((successful / total) * 100);
  }
}
