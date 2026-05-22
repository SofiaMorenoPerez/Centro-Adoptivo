export interface SolicitudModel {
  id: number;
  animalId: number;
  animalName: string;
  adopterId: number;
  adopterUsername: string;
  requestDate: string;
  status: string;
  rejectionReason: string;
  resolutionDate: string;
}
