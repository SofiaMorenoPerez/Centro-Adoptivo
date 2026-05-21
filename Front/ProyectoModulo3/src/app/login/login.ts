import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

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

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ingresar() {
    if (!this.usuario || this.usuario.trim() === '') {
      this.error = 'El usuario es obligatorio';
      return;
    }
    if (!this.contrasena || this.contrasena.trim() === '') {
      this.error = 'La contraseña es obligatoria';
      return;
    }

    this.authService.login(this.usuario, this.contrasena).subscribe({
      next: (response) => {
        this.authService.guardarToken(response.token);
        this.authService.guardarRol(response.role);
        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/usuario']);
        }
      },
      error: (err) => {
        this.error = err.error;
      }
    });
  }

  irSignUp() {
    this.router.navigate(['/sign-up']);
  }

  volver() {
    this.router.navigate(['/inicio']);
  }
}
