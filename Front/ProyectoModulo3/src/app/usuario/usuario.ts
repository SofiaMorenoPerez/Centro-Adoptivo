import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-usuario',
  standalone: false,
  templateUrl: './usuario.html',
  styleUrl: './usuario.css'
})
export class Usuario {
  menuPerfil: boolean = false;

  constructor(private router: Router) {}

  toggleMenu() {
    this.menuPerfil = !this.menuPerfil;
  }

  irDar() {
    this.router.navigate(['/dar']);
  }

  irRecibir() {
    this.router.navigate(['/recibir']);
  }

  irPerfil() {
    this.router.navigate(['/perfil']);
  }

  cerrarSesion() {
    this.router.navigate(['/inicio']);
  }
}
