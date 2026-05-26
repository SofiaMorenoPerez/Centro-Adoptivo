import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NotificacionModel } from '../models/notificacion.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {

  private cliente = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly urlbase: string = 'https://gpcueb.org/sofiamoreno';


  private getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  getMis() {
    return this.cliente.get<NotificacionModel[]>(
      `${this.urlbase}/notificacion/mis`,
      { headers: this.getHeaders() }
    );
  }

  getNoLeidas() {
    return this.cliente.get<NotificacionModel[]>(
      `${this.urlbase}/notificacion/noleidas`,
      { headers: this.getHeaders() }
    );
  }

  contar() {
    return this.cliente.get<number>(
      `${this.urlbase}/notificacion/contar`,
      { headers: this.getHeaders() }
    );
  }

  marcarLeida(id: number) {
    return this.cliente.patch(
      `${this.urlbase}/notificacion/leer/${id}`,
      null,
      { headers: this.getHeaders() }
    );
  }

  marcarTodasLeidas() {
    return this.cliente.patch(
      `${this.urlbase}/notificacion/leer/todas`,
      null,
      { headers: this.getHeaders() }
    );
  }
}
