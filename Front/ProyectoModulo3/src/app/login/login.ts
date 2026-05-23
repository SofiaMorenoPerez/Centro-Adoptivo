import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

declare var bootstrap: any;

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  usuario = '';
  contrasena = '';

  toastMensaje = '';
  toastTitulo = '';
  toastColor = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  mostrarToast(mensaje: string, exito: boolean): void {
    this.toastMensaje = mensaje;
    this.toastTitulo = exito ? '¡Éxito! ✅' : '¡Error! ❌';
    this.toastColor = exito ? '#b5f1ca' : '#ee9fb7';
    this.cdr.detectChanges();
    const toastEl = document.getElementById('loginToast');
    if (toastEl) {
      const toastActual = bootstrap.Toast.getInstance(toastEl);
      if (toastActual) toastActual.dispose();
      const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
      toast.show();
    }
  }

  ingresar() {
    if (!this.usuario || this.usuario.trim() === '') {
      this.mostrarToast('El usuario es obligatorio', false);
      return;
    }
    if (!this.contrasena || this.contrasena.trim() === '') {
      this.mostrarToast('La contraseña es obligatoria', false);
      return;
    }

    this.authService.login(this.usuario, this.contrasena).subscribe({
      next: (response) => {
        this.authService.guardarToken(response.token);
        this.authService.guardarRol(response.role);
        this.authService.guardarId(response.id);
        this.mostrarToast('Bienvenido', true);
        setTimeout(() => {
          if (response.role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/usuario']);
          }
        }, 1500);
      },
      error: (err) => {
        const mensaje = typeof err.error === 'string'
          ? err.error
          : err.message || 'Error al iniciar sesión';
        this.mostrarToast(mensaje, false);
      }
    });
  }

  irSignUp() { this.router.navigate(['/sign-up']); }
  volver() { this.router.navigate(['/inicio']); }
}
