import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProcessDetails } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProcessService {
  private apiUrl = `${environment.apiBaseUrl}/processes`;

  constructor(private http: HttpClient) {}

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
