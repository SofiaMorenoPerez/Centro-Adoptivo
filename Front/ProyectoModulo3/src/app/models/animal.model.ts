export interface AnimalModel {
  id: number;
  name: string;
  age: string;
  sterilized: boolean;
  vaccinated: boolean;
  species: string;
  breed: string;
  color: string;
  observations: string;
  image: string;
  publishedAt: string;
  updatedAt: string;
  classification: string;
  status: string;
  publisherId: number;
  adopterId: number | null;
}
