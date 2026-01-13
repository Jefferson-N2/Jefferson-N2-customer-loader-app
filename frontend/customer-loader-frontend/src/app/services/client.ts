import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ClientDetail, PaginatedResponse, Account, PayrollPayment } from '../models';

/**
 * Servicio para gestionar clientes cargados
 * 
 * Maneja la obtención de clientes por proceso y detalles individuales
 */
@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private readonly apiUrl = `${environment.apiBaseUrl}/clients`;
  private readonly requestTimeout = environment.apiTimeout;

  constructor(private readonly http: HttpClient) { }

  /**
   * Obtiene todos los clientes registrados con paginación
   * 
   * @param page - Número de página (0-indexed)
   * @param size - Cantidad de registros por página
   * @returns Observable con respuesta paginada de todos los clientes
   */
  public getAllClients(
    page: number = 0,
    size: number = 20
  ): Observable<PaginatedResponse<ClientDetail>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<ClientDetail>>(
      this.apiUrl,
      { params }
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener todos los clientes'))
    );
  }

  /**
   * Obtiene los clientes de un proceso de carga con paginación
   * 
   * @param processId - ID único del proceso de carga
   * @param page - Número de página (0-indexed)
   * @param size - Cantidad de registros por página
   * @returns Observable con respuesta paginada de clientes
   * 
   * @throws HttpErrorResponse si falla la solicitud
   */
  public getClients(
    processId: string,
    page: number = 0,
    size: number = 10
  ): Observable<PaginatedResponse<ClientDetail>> {
    if (!processId) {
      return throwError(() => new Error('ID de proceso requerido'));
    }

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<ClientDetail>>(
      `${environment.apiBaseUrl}/processes/${processId}/clients`,
      { params }
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener clientes'))
    );
  }

  /**
   * Obtiene un cliente específico
   * 
   * @param clientId - ID único del cliente
   * @returns Observable con detalles del cliente
   * 
   * @throws HttpErrorResponse si no encuentra el cliente
   */
  public getClient(clientId: string): Observable<ClientDetail> {
    if (!clientId) {
      return throwError(() => new Error('ID de cliente requerido'));
    }

    return this.http.get<ClientDetail>(
      `${this.apiUrl}/${clientId}`
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener cliente'))
    );
  }

  /**
   * Obtiene un cliente por su código
   * 
   * @param clientCode - Código único del cliente
   * @returns Observable con detalles completos del cliente
   * 
   * @throws HttpErrorResponse si no encuentra el cliente
   */
  public getClientByCode(clientCode: string): Observable<ClientDetail> {
    if (!clientCode) {
      return throwError(() => new Error('Código de cliente requerido'));
    }

    return this.http.get<ClientDetail>(
      `${this.apiUrl}/code/${clientCode}`
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener cliente por código'))
    );
  }

  /**
   * Obtiene la cuenta de un cliente
   * 
   * @param clientId - ID único del cliente
   * @returns Observable con datos de la cuenta
   */
  public getAccountByClientId(clientId: number): Observable<Account> {
    if (!clientId) {
      return throwError(() => new Error('ID de cliente requerido'));
    }

    return this.http.get<Account>(
      `${environment.apiBaseUrl}/accounts/client/${clientId}`
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener cuenta del cliente'))
    );
  }

  /**
   * Obtiene el primer pago de una cuenta
   * 
   * @param accountId - ID único de la cuenta
   * @returns Observable con datos del primer pago
   */
  public getFirstPaymentByAccountId(accountId: number): Observable<PayrollPayment> {
    if (!accountId) {
      return throwError(() => new Error('ID de cuenta requerido'));
    }

    return this.http.get<PayrollPayment>(
      `${environment.apiBaseUrl}/accounts/client/${accountId}/first-payment`
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener primer pago'))
    );
  }

  /**
   * Maneja errores HTTP de forma consistente
   * 
   * @private
   */
  private handleError(error: HttpErrorResponse | Error, operation: string): Observable<never> {
    let errorMessage = `Error al ${operation}`;

    if (error instanceof HttpErrorResponse) {
      if (error.error instanceof ErrorEvent) {
        errorMessage = error.error.message;
      } else {
        errorMessage = error.error?.message || `Error HTTP ${error.status}`;
      }
    } else if (error instanceof Error) {
      errorMessage = error.message;
    }

    console.error(`[ClientService] ${operation}:`, error);
    return throwError(() => new Error(errorMessage));
  }
}
