import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  usuario: string = '';
  contrasena: string = '';
  error: string = '';

  constructor(private router: Router) {}

  ingresar() {
    if (this.usuario === 'admin' && this.contrasena === 'admin123') {
      this.router.navigate(['/admin']);
    } else if (this.usuario !== '' && this.contrasena !== '') {
      this.router.navigate(['/usuario']);
    } else {
      this.error = 'Por favor ingresa tu usuario y contraseña.';
    }
  }

  irSignUp() {
    this.router.navigate(['/sign-up']);
  }

  volver() {
    this.router.navigate(['/inicio']);
  }
}
