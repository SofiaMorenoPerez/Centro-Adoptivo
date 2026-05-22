import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private cliente = inject(HttpClient);
  private readonly urlbase: string = 'http://localhost:8081';

  login(username: string, password: string) {
    return this.cliente.post<{ token: string, role: string, id: number }>(
      this.urlbase + '/auth/login',
      { username, password }
    );
  }

  register(username: string, password: string, fullName: string, email: string,
           phone: string, city: string, address: string, age: number) {
    return this.cliente.post(
      this.urlbase + '/auth/register',
      { username, password, fullName, email, phone, city, address, age },
      { responseType: 'text' }
    );
  }

  guardarToken(token: string): void { localStorage.setItem('token', token); }
  guardarRol(role: string): void { localStorage.setItem('role', role); }
  guardarId(id: number): void { localStorage.setItem('id', id.toString()); }

  getToken(): string | null { return localStorage.getItem('token'); }
  getRol(): string | null { return localStorage.getItem('role'); }
  getId(): number { return Number(localStorage.getItem('id')); }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('id');
  }

  estaAutenticado(): boolean { return this.getToken() !== null; }
}
