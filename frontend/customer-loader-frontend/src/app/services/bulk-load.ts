import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BulkLoadResponse, BulkLoadStatistics } from '../models';

@Injectable({
  providedIn: 'root'
})
export class BulkLoadService {
  private apiUrl = `${environment.apiBaseUrl}/bulk-load`;

  constructor(private http: HttpClient) {}

  /**
   * Carga un archivo masivo de clientes
   * @param file Archivo TXT a cargar
   * @returns Observable con estadísticas del proceso
   */
  uploadClients(file: File): Observable<BulkLoadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<BulkLoadResponse>(
      `${this.apiUrl}/clients`,
      formData
    );
  }

  /**
   * Obtiene las estadísticas de un proceso de carga
   * @param processId ID del proceso
   * @returns Observable con estadísticas
   */
  getStatistics(processId: string): Observable<BulkLoadStatistics> {
    return this.http.get<BulkLoadStatistics>(
      `${this.apiUrl}/statistics/${processId}`
    );
  }
}
