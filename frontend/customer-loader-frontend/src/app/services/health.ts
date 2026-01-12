import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { HealthResponse } from '../models';

/**
 * Servicio para consultar el estado de salud de la aplicación
 */
@Injectable({
  providedIn: 'root',
})
export class HealthService {
  private readonly apiUrl = `${environment.apiBaseUrl}/health`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene el estado general de salud (app + database)
   */
  getHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(this.apiUrl).pipe(
      catchError(() => of({
        status: 'DOWN',
        timestamp: new Date().toISOString(),
        service: 'customer-loader-backend',
        version: '1.0.0',
        checks: { database: 'DOWN' }
      } as HealthResponse))
    );
  }

  /**
   * Obtiene el estado de disponibilidad (readiness probe)
   */
  getReadiness(): Observable<any> {
    return this.http.get(`${this.apiUrl}/ready`).pipe(
      catchError(() => of({ status: 'NOT_READY' }))
    );
  }

  /**
   * Obtiene el estado de vida (liveness probe)
   */
  getLiveness(): Observable<any> {
    return this.http.get(`${this.apiUrl}/live`).pipe(
      catchError(() => of({ status: 'NOT_ALIVE' }))
    );
  }
}
