import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SolicitudModel } from '../models/solicitud.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class SolicitudService {

  private cliente = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly urlbase: string = 'https://gpcueb.org/sofiamoreno';

  private getHeaders() {
    return new HttpHeaders({
      'Authorization': 'Bearer ' + this.authService.getToken()
    });
  }

  crear(animalId: number) {
    return this.cliente.post<SolicitudModel>(
      `${this.urlbase}/solicitud/crear?animalId=${animalId}`,
      null,
      { headers: this.getHeaders() }
    );
  }

  getMisSolicitudes() {
    return this.cliente.get<SolicitudModel[]>(
      `${this.urlbase}/solicitud/missolicitudes`,
      { headers: this.getHeaders() }
    );
  }

  getByAnimal(animalId: number) {
    return this.cliente.get<SolicitudModel[]>(
      `${this.urlbase}/solicitud/animal/${animalId}`,
      { headers: this.getHeaders() }
    );
  }


  getPendientes() {
    return this.cliente.get<SolicitudModel[]>(
      `${this.urlbase}/solicitud/pendientes`,
      { headers: this.getHeaders() }
    );
  }

  aprobar(id: number) {
    return this.cliente.patch<SolicitudModel>(
      `${this.urlbase}/solicitud/aprobar/${id}`,
      null,
      { headers: this.getHeaders() }
    );
  }

  rechazar(id: number, razon: string) {
    return this.cliente.patch<SolicitudModel>(
      `${this.urlbase}/solicitud/rechazar/${id}?rejectionReason=${encodeURIComponent(razon)}`,
      null,
      { headers: this.getHeaders() }
    );
  }
}
