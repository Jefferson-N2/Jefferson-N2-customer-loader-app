import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ClientDetail, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = `${environment.apiBaseUrl}/clients`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene los clientes de un proceso de carga
   * @param processId ID del proceso
   * @param page Número de página (0-indexed)
   * @param size Cantidad de registros por página
   * @returns Observable con clientes paginados
   */
  getClients(processId: string, page: number = 0, size: number = 10): Observable<PaginatedResponse<ClientDetail>> {
    let params = new HttpParams();
    params = params.set('processId', processId);
    params = params.set('page', page.toString());
    params = params.set('size', size.toString());

    return this.http.get<PaginatedResponse<ClientDetail>>(
      this.apiUrl,
      { params }
    );
  }

  /**
   * Obtiene un cliente específico
   * @param clientId ID del cliente
   * @returns Observable con detalles del cliente
   */
  getClient(clientId: string): Observable<ClientDetail> {
    return this.http.get<ClientDetail>(
      `${this.apiUrl}/${clientId}`
    );
  }
}
