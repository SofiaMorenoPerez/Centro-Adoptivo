import { Component } from '@angular/core';
import { Router } from '@angular/router';

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

  constructor(private router: Router) {}

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
    this.exito = '¡Animal registrado exitosamente! Pronto será revisado por nuestro equipo.';
    setTimeout(() => {
      this.router.navigate(['/usuario']);
    }, 2500);
  }

  volver() {
    this.router.navigate(['/usuario']);
  }
}
