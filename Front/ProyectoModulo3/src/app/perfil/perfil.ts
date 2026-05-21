import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: false,
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class Perfil {

  username: string = 'isa123';
  fullName: string = 'Isabella Moreno';
  email: string = 'isa@gmail.com';
  phone: string = '3001234567';
  city: string = 'Bogotá';
  address: string = 'Calle 123';
  age: number = 20;
  editando: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  activarEdicion() {
    this.editando = true;
  }

  guardarCambios() {
    this.editando = false;
  }

  eliminarCuenta() {
    this.authService.logout();
    this.router.navigate(['/inicio']);
  }

  volver() {
    this.router.navigate(['/usuario']);
  }
}
