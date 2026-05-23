import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

declare var bootstrap: any;

@Component({
  selector: 'app-sign-up',
  standalone: false,
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class SignUp {

  username = '';
  password = '';
  confirmarPassword = '';
  fullName = '';
  email = '';
  phone = '';
  city = '';
  address = '';
  age = 0;

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
    const toastEl = document.getElementById('signupToast');
    if (toastEl) {
      const toastActual = bootstrap.Toast.getInstance(toastEl);
      if (toastActual) toastActual.dispose();
      const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
      toast.show();
    }
  }

  registrar(): void {
    if (!this.username || this.username.trim() === '') {
      this.mostrarToast('El usuario es obligatorio', false);
      return;
    }
    if (!this.password || this.password.trim() === '') {
      this.mostrarToast('La contraseña es obligatoria', false);
      return;
    }
    if (this.password !== this.confirmarPassword) {
      this.mostrarToast('Las contraseñas no coinciden', false);
      return;
    }
    if (!this.fullName || this.fullName.trim() === '') {
      this.mostrarToast('El nombre es obligatorio', false);
      return;
    }
    if (!this.email || this.email.trim() === '') {
      this.mostrarToast('El email es obligatorio', false);
      return;
    }
    if (!this.phone || this.phone.trim() === '') {
      this.mostrarToast('El teléfono es obligatorio', false);
      return;
    }
    if (!this.city || this.city.trim() === '') {
      this.mostrarToast('La ciudad es obligatoria', false);
      return;
    }
    if (!this.address || this.address.trim() === '') {
      this.mostrarToast('La dirección es obligatoria', false);
      return;
    }
    if (this.age <= 0) {
      this.mostrarToast('La edad es obligatoria', false);
      return;
    }

    this.authService.register(
      this.username,
      this.password,
      this.fullName,
      this.email,
      this.phone,
      this.city,
      this.address,
      this.age
    ).subscribe({
      next: () => {
        this.mostrarToast('Usuario registrado exitosamente', true);
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        const mensaje = typeof err.error === 'string'
          ? err.error
          : 'Error al registrar';
        this.mostrarToast(mensaje, false);
      }
    });
  }

  irLogin(): void { this.router.navigate(['/login']); }
}
