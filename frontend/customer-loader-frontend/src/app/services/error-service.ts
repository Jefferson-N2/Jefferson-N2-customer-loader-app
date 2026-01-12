import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BulkLoadError, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private apiUrl = `${environment.apiBaseUrl}/errors`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene los errores de un proceso de carga
   * @param processId ID del proceso
   * @param page Número de página (0-indexed)
   * @param size Cantidad de registros por página
   * @returns Observable con errores paginados
   */
  getErrors(processId: string, page: number = 0, size: number = 10): Observable<PaginatedResponse<BulkLoadError>> {
    let params = new HttpParams();
    params = params.set('processId', processId);
    params = params.set('page', page.toString());
    params = params.set('size', size.toString());

    return this.http.get<PaginatedResponse<BulkLoadError>>(
      this.apiUrl,
      { params }
    );
  }
}
