import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnimalService } from '../services/animal.service';
import { SolicitudService } from '../services/solicitud.service';
import { AnimalModel } from '../models/animal.model';

@Component({
  selector: 'app-recibir',
  standalone: false,
  templateUrl: './recibir.html',
  styleUrl: './recibir.css'
})
export class Recibir implements OnInit {
  filtroEspecie: string = '';
  filtroEdad: string = '';

  animales: AnimalModel[] = [];
  animalesFiltrados: AnimalModel[] = [];
  cargando: boolean = false;
  error: string = '';
  exito: string = '';

  constructor(
    private router: Router,
    private animalService: AnimalService,
    private solicitudService: SolicitudService
  ) {}

  ngOnInit(): void {
    this.cargarAnimales();
  }

  cargarAnimales(): void {
    this.cargando = true;
    this.animalService.getAll().subscribe({
      next: (animales) => {
        this.animales = animales;
        this.aplicarFiltros();
        this.cargando = false;
      },
      error: () => {
        this.error = 'Error al cargar los animales.';
        this.cargando = false;
      }
    });
  }

  aplicarFiltros(): void {
    this.animalesFiltrados = this.animales.filter(animal => {
      const coincideEspecie = this.filtroEspecie === '' ||
        animal.species?.toLowerCase() === this.filtroEspecie.toLowerCase();
      const coincideEdad = this.filtroEdad === '' ||
        animal.age === this.filtroEdad;
      return coincideEspecie && coincideEdad;
    });
  }

  adoptar(animalId: number): void {
    this.error = '';
    this.exito = '';
    this.solicitudService.crear(animalId).subscribe({
      next: () => {
        this.exito = '¡Solicitud enviada! El admin revisará tu solicitud pronto.';
      },
      error: (err) => {
        this.error = err.error || 'Error al enviar la solicitud.';
      }
    });
  }

  volver(): void {
    this.router.navigate(['/usuario']);
  }
}
