export interface AnimalModel {

  id: number;
  nombre: string;
  edad: string;
  esterilizado: boolean;
  vacunado: boolean;
  especie: string;
  raza: string;
  color: string;
  observaciones: string;
  imagen: string;
  publicadoEn: string;
  actualizadoEn: string;
  clasificacion: string;
  estado: string;
  publicadorId: number;
  adoptanteId: number | null;
}
