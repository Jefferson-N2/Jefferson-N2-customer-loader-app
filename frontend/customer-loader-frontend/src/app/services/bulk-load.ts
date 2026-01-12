import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { BulkLoadResponse, BulkLoadStatistics } from '../models';

/**
 * Servicio para gestionar la carga masiva de clientes
 * 
 * Responsabilidades:
 * - Subir archivos de carga masiva
 * - Obtener estadísticas de procesos
 * 
 * @example
 * ```typescript
 * constructor(private bulkLoadService: BulkLoadService) {}
 * 
 * uploadFile(file: File) {
 *   this.bulkLoadService.uploadClients(file).subscribe({
 *     next: (response) => console.log('Carga exitosa:', response),
 *     error: (error) => console.error('Error:', error)
 *   });
 * }
 * ```
 */
@Injectable({
  providedIn: 'root'
})
export class BulkLoadService {
  private readonly apiUrl = `${environment.apiBaseUrl}/bulk-load`;
  private readonly requestTimeout = environment.apiTimeout;

  constructor(private readonly http: HttpClient) {}

  /**
   * Carga un archivo masivo de clientes al servidor
   * 
   * El archivo se envía como InputStream en el body con fileName en query params
   * 
   * @param file - Archivo TXT con datos de clientes
   * @returns Observable con la respuesta de carga
   * @throws HttpErrorResponse si falla la carga
   * 
   * @example
   * ```typescript
   * this.bulkLoadService.uploadClients(file).subscribe(
   *   response => console.log(`Cargados: ${response.successCount} clientes`),
   *   error => console.error(`Error: ${error.message}`)
   * );
   * ```
   */
  public uploadClients(file: File): Observable<BulkLoadResponse> {
    if (!file) {
      return throwError(() => new Error('Archivo no proporcionado'));
    }

    // Construir URL con fileName como query parameter
    const url = `${this.apiUrl}/clients?fileName=${encodeURIComponent(file.name)}`;

    return this.http.post<BulkLoadResponse>(
      url,
      file
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'cargar archivo'))
    );
  }

  /**
   * Obtiene las estadísticas de un proceso de carga
   * 
   * @param processId - ID único del proceso de carga
   * @returns Observable con estadísticas del proceso
   * @throws HttpErrorResponse si no encuentra el proceso
   */
  public getStatistics(processId: string): Observable<BulkLoadStatistics> {
    if (!processId) {
      return throwError(() => new Error('ID de proceso requerido'));
    }

    return this.http.get<BulkLoadStatistics>(
      `${this.apiUrl}/statistics/${processId}`
    ).pipe(
      timeout(this.requestTimeout),
      catchError(error => this.handleError(error, 'obtener estadísticas'))
    );
  }

  /**
   * Maneja errores HTTP de forma consistente
   * 
   * @private
   * @param error - Error HTTP o genérico
   * @param operation - Descripción de la operación que falló
   * @returns Observable que emite un error
   */
  private handleError(error: HttpErrorResponse | Error, operation: string): Observable<never> {
    let errorMessage = `Error al ${operation}`;

    if (error instanceof HttpErrorResponse) {
      if (error.error instanceof ErrorEvent) {
        // Error del lado del cliente
        errorMessage = error.error.message;
      } else {
        // Error del servidor
        errorMessage = error.error?.message || `Error HTTP ${error.status}`;
      }
    } else if (error instanceof Error) {
      errorMessage = error.message;
    }

    console.error(`[BulkLoadService] ${operation}:`, error);
    return throwError(() => new Error(errorMessage));
  }
}
