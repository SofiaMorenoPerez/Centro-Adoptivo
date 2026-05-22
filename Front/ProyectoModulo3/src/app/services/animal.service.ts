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
      'Authorization': 'Bearer ' + this.authService.getToken()
    });
  }

  // ─── USUARIO ────────────────────────────────────────────────────────────────

  // Registrar animal con foto (multipart/form-data)
  // El back espera: @RequestPart("data") AnimalDTO + @RequestPart("image") MultipartFile
  registrar(datos: {
    name: string;
    age: string;
    sterilized: boolean;
    vaccinated: boolean;
    observations: string;
  }, imagen: File) {
    const formData = new FormData();

    // El back espera "data" como JSON en una parte del multipart
    const blob = new Blob([JSON.stringify(datos)], { type: 'application/json' });
    formData.append('data', blob);

    // El back espera "image" como archivo
    formData.append('image', imagen);

    // OJO: NO ponemos Content-Type manualmente, el browser lo pone solo con el boundary
    return this.cliente.post<AnimalModel>(
      this.urlbase + '/animal/registrar',
      formData,
      { headers: new HttpHeaders({ 'Authorization': 'Bearer ' + this.authService.getToken() }) }
    );
  }

  // Animales disponibles para adopción (vista recibir)
  getAll() {
    return this.cliente.get<AnimalModel[]>(
      this.urlbase + '/animal/getall',
      { headers: this.getHeaders() }
    );
  }

  // Mis publicaciones (animales que yo publiqué)
  getMisPublicaciones() {
    return this.cliente.get<AnimalModel[]>(
      this.urlbase + '/animal/mispublicaciones',
      { headers: this.getHeaders() }
    );
  }

  // Obtener animal por ID
  getById(id: number) {
    return this.cliente.get<AnimalModel>(
      this.urlbase + '/animal/getbyid/' + id,
      { headers: this.getHeaders() }
    );
  }

  // ─── ADMIN ──────────────────────────────────────────────────────────────────

  // Todos los animales sin filtro (admin)
  getAllAdmin() {
    return this.cliente.get<AnimalModel[]>(
      this.urlbase + '/animal/getallAdmin',
      { headers: this.getHeaders() }
    );
  }

  // Actualizar animal
  update(id: number, datos: Partial<AnimalModel>) {
    return this.cliente.put<AnimalModel>(
      this.urlbase + '/animal/update/' + id,
      datos,
      { headers: this.getHeaders() }
    );
  }

  // Eliminar animal
  delete(id: number) {
    return this.cliente.delete(
      this.urlbase + '/animal/delete/' + id,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }
}
