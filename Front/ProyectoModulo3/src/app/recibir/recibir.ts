import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
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

  animales: AnimalModel[] = [];
  animalesFiltrados: AnimalModel[] = [];
  cargando = false;
  error = '';
  exito = '';

  constructor(
    private router: Router,
    private animalService: AnimalService,
    private solicitudService: SolicitudService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAnimales();
  }

  cargarAnimales(): void {
    this.cargando = true;
    this.animalService.getAll().subscribe({
      next: (animales) => {
        this.animales = animales || [];
        this.animalesFiltrados = [...this.animales];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.animales = [];
        this.animalesFiltrados = [];
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  adoptar(animalId: number): void {
    this.error = '';
    this.exito = '';
    this.solicitudService.crear(animalId).subscribe({
      next: () => {
        this.exito = '¡Solicitud enviada! El admin revisará tu solicitud pronto.';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = typeof err.error === 'string' ? err.error : 'Error al enviar la solicitud.';
        this.cdr.detectChanges();
      }
    });
  }

  volver(): void {
    this.router.navigate(['/usuario']);
  }
}
