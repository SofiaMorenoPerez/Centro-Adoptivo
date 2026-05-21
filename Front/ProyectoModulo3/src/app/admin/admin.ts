import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin {
  vista: string = 'transacciones';
  menuAbierto: boolean = false;

  constructor(private router: Router) {}

  cambiarVista(v: string) {
    this.vista = v;
    this.menuAbierto = false;
  }

  cerrarSesion() {
    this.router.navigate(['/inicio']);
  }
}
