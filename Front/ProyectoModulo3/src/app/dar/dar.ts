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
    // Validaciones locales
    if (!this.imagenArchivo) {
      this.error = 'La foto del animal es obligatoria.';
      this.exito = '';
      return;
    }
    if (!this.nombre || this.nombre.trim() === '') {
      this.error = 'El nombre del animal es obligatorio.';
      this.exito = '';
      return;
    }
    if (!this.edad) {
      this.error = 'La edad del animal es obligatoria.';
      this.exito = '';
      return;
    }

    this.error = '';
    this.exito = '';
    this.cargando = true;

    // Armar el objeto que espera el back como @RequestPart("data")
    const datos = {
      name: this.nombre.trim(),
      age: this.edad,
      sterilized: this.esterilizado,
      vaccinated: this.vacunado,
      observations: this.observaciones.trim()
    };

    this.animalService.registrar(datos, this.imagenArchivo).subscribe({
      next: () => {
        this.cargando = false;
        this.exito = '¡Animal registrado exitosamente! La IA lo validó correctamente.';
        this.error = '';
        setTimeout(() => {
          this.router.navigate(['/usuario']);
        }, 2500);
      },
      error: (err) => {
        this.cargando = false;
        // El back devuelve mensajes de texto plano en los errores
        this.error = err.error || 'Error al registrar el animal. Intenta de nuevo.';
        this.exito = '';
      }
    });
  }

  volver() {
    this.router.navigate(['/usuario']);
  }
}
