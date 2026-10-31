import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CrearEnvioRequest, EnvioResponse } from '../models/envio.model';
import { API_URL } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class EnvioService {
  private http = inject(HttpClient);
  private apiUrl = `${API_URL}/envios`;

  crear(datos: CrearEnvioRequest): Observable<EnvioResponse> {
    return this.http.post<EnvioResponse>(this.apiUrl, datos);
  }
}