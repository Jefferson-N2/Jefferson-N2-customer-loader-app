import { Component, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil, tap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { ClientService } from '../../../../services/client';
import { PaginatedResponse, ClientDetail } from '../../../../models';
import { ClientDetailsDialogComponent } from '../process-history/client-details-dialog/client-details-dialog';

/**
 * Componente que muestra todos los clientes cargados en el sistema
 * 
 * Características:
 * - Tabla con todos los clientes
 * - Paginación
 * - Modal con detalles del cliente
 * - Estados: cargando, vacío, error
 */
@Component({
  selector: 'app-all-clients',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatTooltipModule
  ],
  templateUrl: './all-clients.html',
  styleUrl: './all-clients.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllClientsComponent implements OnDestroy {
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
    'idType',
    'idNumber',
    'email',
    'phoneNumber',
    'status',
    'acciones'
  ];

  /** Tamaño de página actual */
  pageSize = 5;
  
  /** Opciones disponibles de tamaño de página */
  readonly pageSizeOptions = [5, 10, 25, 50];
  
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
    private readonly dialog: MatDialog,
    private readonly cdr: ChangeDetectorRef,
    private readonly router: Router
  ) {
    this.clients$ = this.clientsSubject$.asObservable();
    this.loadClients();
  }

  /**
   * Carga todos los clientes del servidor
   * 
   * @private
   */
  private loadClients(): void {
    this.isLoading$.next(true);
    this.error$.next(null);

    this.clientService
      .getAllClients(this.currentPage, this.pageSize)
      .pipe(
        tap(data => {
          this.clientsSubject$.next(data);
          this.isLoading$.next(false);
          this.cdr.markForCheck();
        }),
        catchError(error => {
          this.isLoading$.next(false);
          const errorMsg = error?.message || 'Error al cargar clientes';
          this.error$.next(errorMsg);
          this.cdr.markForCheck();
          return of(this.createEmptyResponse());
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  /**
   * Carga más clientes (página siguiente)
   */
  loadMore(): void {
    this.currentPage++;
    this.loadClients();
  }


  /**
   * Verifica si hay más clientes para cargar
   */
  canLoadMore(clients: PaginatedResponse<ClientDetail>): boolean {
    if (!clients || !clients.content) {
      return false;
    }
    return clients.content.length < clients.totalElements;
  }

  /**
   * Abre modal con detalles del cliente
   * 
   * @param client - Cliente a mostrar
   */
  viewDetails(client: ClientDetail): void {
    this.dialog.open(ClientDetailsDialogComponent, {
      width: '95%',
      maxWidth: '1000px',
      data: { clientDetail: client }
    });
  }

  /**
   * Función trackBy para optimizar renderizado de filas
   * 
   * @param index - Índice de la fila
   * @param item - Cliente a renderizar
   */
  trackByClientCode(_index: number, item: ClientDetail): string {
    return item.clientCode || '';
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

  /**
   * Vuelve atrás al dashboard
   */
  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
