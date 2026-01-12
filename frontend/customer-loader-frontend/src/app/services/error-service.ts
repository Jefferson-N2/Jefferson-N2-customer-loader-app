import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { BulkLoadError, PaginatedResponse } from '../models';

/**
 * Servicio para gestionar errores de carga masiva
 * 
 * Maneja la obtención de errores validación en procesos de carga
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private readonly apiUrl = `${environment.apiBaseUrl}/processes`;
  private readonly requestTimeout = environment.apiTimeout;

  constructor(private readonly http: HttpClient) {}

  /**
   * Obtiene los errores de un proceso de carga con paginación
   * 
   * @param processId - ID único del proceso de carga
   * @param page - Número de página (0-indexed)
   * @param size - Cantidad de registros por página
   * @returns Observable con respuesta paginada de errores
   * 
   * @throws HttpErrorResponse si falla la solicitud
   */
  public getErrors(
    processId: string,
    page: number = 0,
    size: number = 10
  ): Observable<PaginatedResponse<BulkLoadError>> {
    if (!processId) {
      return throwError(() => new Error('ID de proceso requerido'));
    }

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<BulkLoadError>>(
      `${this.apiUrl}/${processId}/errors`,
      { params }
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener errores'))
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

    console.error(`[ErrorService] ${operation}:`, error);
    return throwError(() => new Error(errorMessage));
  }
}
