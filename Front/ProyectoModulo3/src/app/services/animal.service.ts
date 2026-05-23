import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AnimalModel } from '../models/animal.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AnimalService {

  private cliente = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly urlbase: string = 'http://localhost:8081';

  private getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  registrar(datos: {
    name: string;
    age: string;
    species: string;
    breed: string;
    color: string;
    classification: string;
    sterilized: boolean;
    vaccinated: boolean;
    observations: string;
  }, imagen: File) {
    const formData = new FormData();
    const blob = new Blob([JSON.stringify(datos)], { type: 'application/json' });
    formData.append('data', blob);
    formData.append('image', imagen);

    // ⚠️ NO pongas Content-Type manualmente — deja que el navegador lo genere
    // con el boundary correcto para multipart/form-data
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    return this.cliente.post<AnimalModel>(
      `${this.urlbase}/animal/registrar`,
      formData,
      { headers }  // solo Authorization, sin Content-Type
    );
  }

  getAll() {
    return this.cliente.get<AnimalModel[]>(
      `${this.urlbase}/animal/getall`,
      { headers: this.getHeaders() }
    );
  }

  getMisPublicaciones() {
    return this.cliente.get<AnimalModel[]>(
      `${this.urlbase}/animal/mispublicaciones`,
      { headers: this.getHeaders() }
    );
  }

  getById(id: number) {
    return this.cliente.get<AnimalModel>(
      `${this.urlbase}/animal/getbyid/${id}`,
      { headers: this.getHeaders() }
    );
  }

  getAllAdmin() {
    return this.cliente.get<AnimalModel[]>(
      `${this.urlbase}/animal/getallAdmin`,
      { headers: this.getHeaders() }
    );
  }

  update(id: number, datos: Partial<AnimalModel>) {
    return this.cliente.put<AnimalModel>(
      `${this.urlbase}/animal/update/${id}`,
      datos,
      { headers: this.getHeaders() }
    );
  }

  delete(id: number) {
    return this.cliente.delete(
      `${this.urlbase}/animal/delete/${id}`,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }
}
