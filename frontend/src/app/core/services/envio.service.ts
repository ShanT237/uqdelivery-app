import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CrearEnvioRequest, EnvioResponse } from '../models/envio.model';

@Injectable({ providedIn: 'root' })
export class EnvioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/envios';

  crear(datos: CrearEnvioRequest): Observable<EnvioResponse> {
    return this.http.post<EnvioResponse>(this.apiUrl, datos);
  }
}