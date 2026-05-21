import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-recibir',
  standalone: false,
  templateUrl: './recibir.html',
  styleUrl: './recibir.css'
})
export class Recibir {
  filtroEspecie: string = '';
  filtroEdad: string = '';

  constructor(private router: Router) {}

  volver() {
    this.router.navigate(['/usuario']);
  }
}
