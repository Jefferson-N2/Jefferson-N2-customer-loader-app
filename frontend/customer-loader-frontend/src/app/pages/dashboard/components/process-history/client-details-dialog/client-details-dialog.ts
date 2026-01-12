import { Component, Inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { ClientService } from '../../../../../services/client';
import { ClientDetail } from '../../../../../models';

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
export class ClientDetailsDialogComponent implements OnInit {
  /** Datos del cliente */
  readonly clientDetails$ = new BehaviorSubject<ClientDetail | null>(null);
  
  /** Estado de carga */
  readonly isLoading$ = new BehaviorSubject<boolean>(false);
  
  /** Mensaje de error */
  readonly error$ = new BehaviorSubject<string | null>(null);

  /** Columnas para tabla de pagos (simulada) */
  readonly paymentColumns = ['date', 'amount', 'status'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { clientDetail: ClientDetail },
    private readonly clientService: ClientService
  ) {
    this.clientDetails$.next(data.clientDetail);
  }

  ngOnInit(): void {
    // Los datos ya están cargados desde el dialog data
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
    const client = this.clientDetails$.value;
    if (!client?.account?.accountNumber) {
      return [];
    }
    
    // TODO: Implementar llamada real al API cuando esté disponible
    // return this.paymentService.getPaymentHistory(client.account.accountNumber);
    
    // Datos de ejemplo basados en la cuenta del cliente
    return [
      {
        date: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000),
        amount: client.account.payrollValue || 2500000,
        status: 'COMPLETED'
      },
      {
        date: new Date(Date.now() - 60 * 24 * 60 * 60 * 1000),
        amount: client.account.payrollValue || 2500000,
        status: 'COMPLETED'
      },
      {
        date: new Date(Date.now() - 90 * 24 * 60 * 60 * 1000),
        amount: client.account.payrollValue || 2500000,
        status: 'COMPLETED'
      }
    ];
  }

  /**
   * Formatea moneda
   */
  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return 'N/A';
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }
}
