import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserModel } from '../models/user.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private cliente = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly urlbase: string = 'http://localhost:8081';

  private getHeaders() {
    return new HttpHeaders({
      'Authorization': 'Bearer ' + this.authService.getToken()
    });
  }

  // Obtener todos los usuarios (ADMIN)
  getPerfil() {
    const id = this.authService.getId();
    return this.cliente.get<UserModel>(
      this.urlbase + '/usuario/perfil/' + id,
      { headers: this.getHeaders() }
    );
  }

  update(usuario: UserModel) {
    const id = this.authService.getId();
    return this.cliente.put(
      this.urlbase + '/usuario/editar/' + id,
      usuario,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  delete(id: number) {
    return this.cliente.delete(
      this.urlbase + '/usuario/deletebyid/' + id,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  getAll() {
    return this.cliente.get<UserModel[]>(
      this.urlbase + '/usuario/getall',
      { headers: this.getHeaders() }
    );
  }

  updateAdmin(id: number, usuario: UserModel) {
    return this.cliente.put(
      this.urlbase + '/usuario/updatejson?id=' + id,
      usuario,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  cambiarRol(id: number, rol: string) {
    return this.cliente.patch(
      `${this.urlbase}/usuario/rol/${id}?rol=${rol}`,
      null,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

}


