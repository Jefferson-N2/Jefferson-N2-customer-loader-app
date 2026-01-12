import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProcessDetails, BulkLoadProcess, PaginatedResponse, BulkLoadError } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProcessService {
  private apiUrl = `${environment.apiBaseUrl}/processes`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene todos los procesos de carga paginados
   * @param page número de página
   * @param size tamaño de página
   * @returns Observable con respuesta paginada
   */
  getAllProcesses(page: number = 0, size: number = 20): Observable<PaginatedResponse<BulkLoadProcess>> {
    return this.http.get<PaginatedResponse<BulkLoadProcess>>(
      `${this.apiUrl}?page=${page}&size=${size}`
    );
  }

  /**
   * Obtiene los detalles de un proceso específico (sin detalles completos)
   * @param processId ID del proceso
   * @returns Observable con detalles del proceso
   */
  getProcessById(processId: string): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}?processId=${processId}`
    );
  }

  /**
   * Obtiene los detalles completos de un proceso de carga
   * @param processId ID del proceso
   * @returns Observable con detalles completos
   */
  getProcessDetails(processId: string): Observable<ProcessDetails> {
    return this.http.get<ProcessDetails>(
      `${this.apiUrl}/${processId}/details`
    );
  }

  /**
   * Obtiene los errores de un proceso paginados
   * @param processId ID del proceso
   * @param page número de página
   * @param size tamaño de página
   * @returns Observable con errores paginados
   */
  getProcessErrors(processId: string, page: number = 0, size: number = 10): Observable<PaginatedResponse<BulkLoadError>> {
    return this.http.get<PaginatedResponse<BulkLoadError>>(
      `${this.apiUrl}/${processId}/errors?page=${page}&size=${size}`
    );
  }

  /**
   * Obtiene el estado de un proceso
   * @param processId ID del proceso
   * @returns Observable con estado del proceso
   */
  getProcessStatus(processId: string): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/${processId}/status`
    );
  }
}
