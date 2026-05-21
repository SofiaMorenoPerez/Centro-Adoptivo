import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-inicio',
  standalone: false,
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class Inicio {
  titulo: string = 'Centro de Adopción SIS Pets';
  subtitulo: string = 'Cada patita merece un hogar lleno de amor 🐾';

  constructor(private router: Router) {}

  irLogin() {
    this.router.navigate(['/login']);
  }

  irLoginAdmin() {
    this.router.navigate(['/admin-login']);
  }
}
