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

  // Perfil del usuario autenticado
  getPerfil() {
    const id = this.authService.getId();
    return this.cliente.get<UserModel>(
      this.urlbase + '/usuario/getbyid/' + id,
      { headers: this.getHeaders() }
    );
  }

  // Obtener todos los usuarios (ADMIN)
  getAll() {
    return this.cliente.get<UserModel[]>(
      this.urlbase + '/usuario/getall',
      { headers: this.getHeaders() }
    );
  }

  // Crear admin (ADMIN)
  crear(usuario: UserModel) {
    return this.cliente.post(
      this.urlbase + '/usuario/createjson',
      usuario,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  // Actualizar usuario
  update(usuario: UserModel) {
    const id = this.authService.getId();
    return this.cliente.put(
      this.urlbase + '/usuario/updatejson?id=' + id,
      usuario,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  // Eliminar usuario
  delete(id: number) {
    return this.cliente.delete(
      this.urlbase + '/usuario/deletebyid/' + id,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }
}
