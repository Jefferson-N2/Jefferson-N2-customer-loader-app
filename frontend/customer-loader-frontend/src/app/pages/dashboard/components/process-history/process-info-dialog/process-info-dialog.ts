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
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
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
    MatTooltipModule,
    MatPaginatorModule
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
    console.log(' ProcessInfoDialogComponent constructor - processId:', this.processId);
    this.errors$ = this.errorsSubject$.asObservable();
    this.clients$ = this.clientsSubject$.asObservable();
  }

  ngOnInit(): void {
    console.log(' ProcessInfoDialogComponent ngOnInit called');
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

    console.log(' About to call loadErrors()');
    this.loadErrors();
    console.log(' About to call loadClients()');
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
      .getProcessDetails(this.processId)
      .pipe(
        tap(response => {
          if (response) {
            this.processDetails$.next(response);
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
    console.log('✓✓ loadErrors() INICIO - processId:', this.processId, 'página:', this.errorCurrentPage$.value);
    
    // Usar el endpoint /processes/{processId}/errors que devuelve datos paginados
    this.processService
      .getProcessErrors(this.processId, this.errorCurrentPage$.value, 5)
      .pipe(
        tap((data: any) => {
          console.log('✓✓ API RESPONSE errores recibido:', data);
          if (data) {
            // Adaptarse a la estructura del backend: {errors, totalErrors} o {content, totalElements}
            let errorsData = data.errors || data.content || [];
            const totalErrors = data.totalErrors || data.totalElements || 0;
            
            console.log('✓✓ errorsData extraído:', errorsData);
            console.log('✓✓ totalErrors:', totalErrors);
            
            if (!Array.isArray(errorsData)) {
              errorsData = [];
            }
            
            // Aplicar filtro de tipo ID localmente
            const idTypeFilter = this.errorFilterIdType$.value;
            if (idTypeFilter) {
              errorsData = errorsData.filter((error: any) => 
                error.idType?.toUpperCase() === idTypeFilter.toUpperCase()
              );
            }
            
            // NO acumular datos, reemplazar completamente
            const processedData = {
              content: errorsData,
              totalElements: totalErrors,
              totalPages: Math.ceil(totalErrors / 5),
              size: 5,
              number: this.errorCurrentPage$.value,
              empty: errorsData.length === 0
            };
            
            console.log('✓✓ processedData errores FINAL:', processedData);
            this.errorsSubject$.next(processedData);
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
          console.log('✓✓ errorsSubject$ actualizado y markForCheck() llamado');
        }),
        catchError((error) => {
          console.error('✗✗ ERROR EN loadErrors():', error);
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
   * Maneja el cambio de página en la tabla de errores
   */
  onErrorPageChange(event: PageEvent): void {
    this.errorCurrentPage$.next(event.pageIndex);
    this.loadErrors();
  }

  /**
   * Carga clientes del proceso
   */
  private loadClients(): void {
    this.isLoadingClients$.next(true);
    console.log(' loadClients() INICIO - processId:', this.processId);
    
    this.clientService
      .getClients(this.processId, this.clientCurrentPage$.value, 5)
      .pipe(
        tap((data: any) => {
          console.log(' API RESPONSE recibido:', data);
          
          // Adaptarse a la estructura del backend: {clients, totalClients, processId}
          let clientsData = data.clients || data.content || [];
          const totalClients = data.totalClients || data.totalElements || 0;
          
          console.log(' clientsData extraído:', clientsData);
          console.log(' totalClients:', totalClients);
          
          // Aplicar filtros localmente
          const nameFilter = this.clientFilterName$.value?.toLowerCase() || '';
          const codeFilter = this.clientFilterCode$.value?.toLowerCase() || '';
          console.log(' Filtros - nombre:', nameFilter, 'código:', codeFilter);
          
          if (nameFilter || codeFilter) {
            clientsData = clientsData.filter((client: any) => {
              const matchesName = !nameFilter || 
                `${client.firstNames} ${client.lastNames}`.toLowerCase().includes(nameFilter);
              const matchesCode = !codeFilter || 
                client.clientCode?.toLowerCase().includes(codeFilter);
              return matchesName && matchesCode;
            });
            console.log(' clientsData después de filtros:', clientsData);
          }
          
          // NO acumular datos, reemplazar completamente
          const processedData = {
            content: clientsData,
            totalElements: totalClients,
            totalPages: Math.ceil(totalClients / 5),
            size: 5,
            number: this.clientCurrentPage$.value,
            empty: clientsData.length === 0
          };
          
          console.log(' processedData FINAL:', processedData);
          this.clientsSubject$.next(processedData);
          this.isLoadingClients$.next(false);
          this.cdr.markForCheck();
          console.log(' clientsSubject$ actualizado y markForCheck() llamado');
        }),
        catchError((error) => {
          console.error('✗✗ ERROR EN loadClients():', error);
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
   * Maneja el cambio de página en la tabla de clientes
   */
  onClientPageChange(event: PageEvent): void {
    this.clientCurrentPage$.next(event.pageIndex);
    this.loadClients();
  }

  /**
   * Obtiene etiqueta de tipo de ID
   */
  getIdTypeLabel(idType: string): string {
    if (!idType) return 'N/A';
    return idType === 'C' ? 'C (Cédula)' : idType === 'P' ? 'P (Pasaporte)' : idType;
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
