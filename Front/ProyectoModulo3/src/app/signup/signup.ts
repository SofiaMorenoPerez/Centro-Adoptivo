import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-sign-up',
  standalone: false,
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class SignUp {

  username= '';
  password = '';
  confirmarPassword = '';
  fullName = '';
  email = '';
  phone = '';
  city = '';
  address = '';
  age = 0;
  error = '';

  constructor(private router: Router, private authService: AuthService) {}

  registrar(): void {
    if (!this.username || this.username.trim() === '') {
      this.error = 'El usuario es obligatorio';
      return;
    }
    if (!this.password || this.password.trim() === '') {
      this.error = 'La contraseña es obligatoria';
      return;
    }
    if (this.password !== this.confirmarPassword) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }
    if (!this.fullName || this.fullName.trim() === '') {
      this.error = 'El nombre es obligatorio';
      return;
    }
    if (!this.email || this.email.trim() === '') {
      this.error = 'El email es obligatorio';
      return;
    }
    if (!this.phone || this.phone.trim() === '') {
      this.error = 'El teléfono es obligatorio';
      return;
    }
    if (!this.city || this.city.trim() === '') {
      this.error = 'La ciudad es obligatoria';
      return;
    }
    if (!this.address || this.address.trim() === '') {
      this.error = 'La dirección es obligatoria';
      return;
    }
    if (this.age <= 0) {
      this.error = 'La edad es obligatoria';
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
      next: (response) => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.error = error.error;
      }
    });
  }

  irLogin(): void {
    this.router.navigate(['/login']);
  }

}
