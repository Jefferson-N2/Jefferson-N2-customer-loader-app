import { Component, Inject, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ClientService } from '../../../../../services/client';
import { ClientDetail, Account, PayrollPayment } from '../../../../../models';

/**
 * Componente de diálogo para mostrar detalles del cliente
 * 
 * Muestra:
 * - Información personal del cliente
 * - Datos de la cuenta bancaria
 * - Historial de pagos (si disponible)
 */
@Component({
  selector: 'app-client-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule
  ],
  templateUrl: './client-details-dialog.html',
  styleUrl: './client-details-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClientDetailsDialogComponent implements OnInit, OnDestroy {
  /** Datos del cliente */
  readonly clientDetails$ = new BehaviorSubject<ClientDetail | null>(null);
  
  /** Datos de la cuenta */
  readonly account$ = new BehaviorSubject<Account | null>(null);
  
  /** Primer pago */
  readonly firstPayment$ = new BehaviorSubject<PayrollPayment | null>(null);
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(false);
  
  /** Mensaje de error */
  readonly error$ = new BehaviorSubject<string | null>(null);

  /** Columnas para tabla de pagos */
  readonly paymentColumns = ['date', 'amount', 'status'];
  
  /** Subject para desuscripción */
  private readonly destroy$ = new Subject<void>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { clientDetail: ClientDetail },
    private readonly clientService: ClientService
  ) {
    this.clientDetails$.next(data.clientDetail);
  }

  ngOnInit(): void {
    const client = this.clientDetails$.value;
    if (client?.id) {
      this.loadAccountAndPayment(client.id);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Carga la cuenta y el primer pago del cliente
   */
  private loadAccountAndPayment(clientId: number): void {
    this.isLoading$.next(true);
    this.error$.next(null);

    // Cargar cuenta
    this.clientService.getAccountByClientId(clientId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (account) => {
          this.account$.next(account);
          
          // Si hay cuenta, cargar el primer pago
          if (account?.id) {
            this.clientService.getFirstPaymentByAccountId(account.id)
              .pipe(takeUntil(this.destroy$))
              .subscribe({
                next: (payment) => {
                  this.firstPayment$.next(payment);
                  this.isLoading$.next(false);
                },
                error: (err) => {
                  console.error('Error loading payment:', err);
                  this.isLoading$.next(false);
                }
              });
          } else {
            this.isLoading$.next(false);
          }
        },
        error: (err) => {
          console.error('Error loading account:', err);
          this.error$.next('No se pudieron cargar los datos de la cuenta');
          this.isLoading$.next(false);
        }
      });
  }

  /**
   * Formatea fecha para mostrar
   */
  formatDate(date: string | Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * Genera datos de ejemplo para historial de pagos
   */
  getPaymentHistory(): any[] {
    const account = this.account$.value;
    const payment = this.firstPayment$.value;
    
    if (!account?.accountNumber) {
      return [];
    }

    const payments = [];
    
    if (payment) {
      payments.push({
        date: payment.paymentDate,
        amount: payment.amount,
        status: payment.status || 'COMPLETED'
      });
    }

    return payments;
  }

  /**
   * Formatea moneda
   */
  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return 'N/A';
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }
}
