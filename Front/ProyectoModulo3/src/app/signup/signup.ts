import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: false,
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})

export class SignUp {
  nombreCompleto: string = '';
  username: string = '';
  email: string = '';
  telefono: number = 0;
  ciudad: string = '';
  direccion: string = '';
  password: string = '';
  confirmar: string = '';
  error: string = '';
  exito: string = '';

  constructor(private router: Router) {}

  guardar() {
    if (!this.nombreCompleto || !this.username || !this.email ||
      !this.telefono || !this.ciudad || !this.direccion || !this.password) {
      this.error = 'Todos los campos son obligatorios.';
      this.exito = '';
      return;
    }
    if (this.password !== this.confirmar) {
      this.error = 'Las contraseñas no coinciden.';
      this.exito = '';
      return;
    }
    this.error = '';
    this.exito = '¡Cuenta creada exitosamente! Ya puedes iniciar sesión.';
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 2000);
  }

  volver() {
    this.router.navigate(['/login']);
  }
}
