import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProcessDetails, BulkLoadProcess, PaginatedResponse } from '../models';

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
