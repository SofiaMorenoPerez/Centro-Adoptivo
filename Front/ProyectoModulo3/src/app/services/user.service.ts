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

  getPerfil() {
    return this.cliente.get<UserModel>(
      this.urlbase + '/user/profile',
      {
        headers: this.getHeaders()
      }
    );
  }

  update(usuario: UserModel) {
    return this.cliente.put(
      this.urlbase + '/user/update',
      usuario,
      {
        headers: this.getHeaders(),
        responseType: 'text'
      }
    );
  }

  delete(id: number) {
    return this.cliente.delete(
      this.urlbase + '/user/deletebyid/' + id,
      {
        headers: this.getHeaders(),
        responseType: 'text'
      }
    );
  }

}
