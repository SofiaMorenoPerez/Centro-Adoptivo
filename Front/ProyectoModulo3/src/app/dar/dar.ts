import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AnimalService } from '../services/animal.service';

@Component({
  selector: 'app-dar',
  standalone: false,
  templateUrl: './dar.html',
  styleUrl: './dar.css'
})
export class Dar {
  nombre = '';
  edad = '';
  esterilizado= false;
  vacunado = false;
  observaciones = '';
  imagenPreview: string | null = null;
  imagenArchivo: File | null = null;
  error = '';
  exito = '';
  cargando = false;

  constructor(private router: Router, private animalService: AnimalService) {}

  onImagenSeleccionada(event: Event) {
    const input = event.target as HTMLInputElement;
    const archivo = input.files?.[0];
    if (archivo) {
      this.imagenArchivo = archivo;
      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        this.imagenPreview = e.target?.result as string;
      };
      reader.readAsDataURL(archivo);
    }
  }

  guardar() {
    if (!this.imagenArchivo) {
      this.error = 'La foto del animal es obligatoria.';
      return;
    }
    if (!this.nombre || this.nombre.trim() === '') {
      this.error = 'El nombre del animal es obligatorio.';
      return;
    }
    if (!this.observaciones || this.observaciones.trim().length < 10) {
      this.error = 'Las observaciones deben tener mínimo 10 caracteres.';
      return;
    }

    this.error = '';
    this.exito = '';
    this.cargando = true;


    const datos = {
      name: this.nombre.trim(),
      sterilized: this.esterilizado,
      vaccinated: this.vacunado,
      observations: this.observaciones.trim(),

      age: 'ADULT',
      species: 'unknown',
      breed: 'unknown',
      color: 'unknown',
      classification: 'DOMESTIC'
    };

    this.animalService.registrar(datos, this.imagenArchivo).subscribe({
      next: () => {
        this.cargando = false;
        this.exito = '¡Animal registrado! La IA detectó y validó la información automáticamente.';
        setTimeout(() => this.router.navigate(['/usuario']), 4000);
      },
      error: (err) => {
        this.cargando = false;
        this.error = err.error || 'Error al registrar el animal.';
      }
    });
  }

  volver() {
    this.router.navigate(['/usuario']);
  }
}
