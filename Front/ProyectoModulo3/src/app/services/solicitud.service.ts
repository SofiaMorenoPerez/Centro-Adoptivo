import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { SolicitudModel } from '../models/solicitud.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class SolicitudService {

  private cliente = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly urlbase: string = 'http://localhost:8081';

  private getHeaders() {
    return new HttpHeaders({
      'Authorization': 'Bearer ' + this.authService.getToken()
    });
  }

  // ─── USUARIO ────────────────────────────────────────────────────────────────

  // Crear solicitud de adopción para un animal
  // El back espera: @RequestParam Long animalId
  crear(animalId: number) {
    return this.cliente.post<SolicitudModel>(
      this.urlbase + '/solicitud/crear?animalId=' + animalId,
      null,
      { headers: this.getHeaders() }
    );
  }

  // Mis solicitudes (las que yo hice como adoptante)
  getMisSolicitudes() {
    return this.cliente.get<SolicitudModel[]>(
      this.urlbase + '/solicitud/missolicitudes',
      { headers: this.getHeaders() }
    );
  }

  // Solicitudes por animal (para ver quién quiere un animal específico)
  getByAnimal(animalId: number) {
    return this.cliente.get<SolicitudModel[]>(
      this.urlbase + '/solicitud/animal/' + animalId,
      { headers: this.getHeaders() }
    );
  }

  // ─── ADMIN ──────────────────────────────────────────────────────────────────

  // Todas las solicitudes pendientes (tabla transacciones del admin)
  getPendientes() {
    return this.cliente.get<SolicitudModel[]>(
      this.urlbase + '/solicitud/pendientes',
      { headers: this.getHeaders() }
    );
  }

  // Aprobar solicitud
  // El back espera: @PatchMapping("/aprobar/{id}")
  aprobar(id: number) {
    return this.cliente.patch<SolicitudModel>(
      this.urlbase + '/solicitud/aprobar/' + id,
      null,
      { headers: this.getHeaders() }
    );
  }

  // Rechazar solicitud con razón
  // El back espera: @PatchMapping("/rechazar/{id}") + @RequestParam String rejectionReason
  rechazar(id: number, razon: string) {
    return this.cliente.patch<SolicitudModel>(
      this.urlbase + '/solicitud/rechazar/' + id + '?rejectionReason=' + encodeURIComponent(razon),
      null,
      { headers: this.getHeaders() }
    );
  }
}
