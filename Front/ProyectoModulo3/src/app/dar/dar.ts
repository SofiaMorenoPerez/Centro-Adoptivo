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
  nombre: string = '';
  edad: string = '';
  esterilizado: boolean = false;
  vacunado: boolean = false;
  observaciones: string = '';
  imagenPreview: string | null = null;
  imagenArchivo: File | null = null;
  error: string = '';
  exito: string = '';
  cargando: boolean = false;

  constructor(private router: Router, private animalService: AnimalService) {}

  onImagenSeleccionada(event: any) {
    const archivo = event.target.files[0];
    if (archivo) {
      this.imagenArchivo = archivo;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagenPreview = e.target.result;
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
