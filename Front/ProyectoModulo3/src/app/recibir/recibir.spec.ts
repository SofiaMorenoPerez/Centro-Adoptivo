import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Recibir } from './recibir';
import { AnimalService } from '../services/animal.service';
import { SolicitudService } from '../services/solicitud.service';

describe('Recibir', () => {

  let component: Recibir;
  let fixture: ComponentFixture<Recibir>;

  let animalServiceMock: { getAll: ReturnType<typeof vi.fn> };
  let solicitudServiceMock: { crear: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {

    animalServiceMock = {
      getAll: vi.fn().mockReturnValue(of([
        {
          id: 1,
          name: 'Max',
          species: 'Perro',
          breed: 'Labrador',
          age: 'Adulto',
          color: 'Negro',
          sterilized: true,
          vaccinated: true,
          observations: 'Muy amigable',
          image: '/img/max.jpg'
        }
      ]))
    };

    solicitudServiceMock = {
      crear: vi.fn().mockReturnValue(of({}))
    };

    routerMock = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [Recibir],
      imports: [FormsModule],
      providers: [
        { provide: AnimalService, useValue: animalServiceMock },
        { provide: SolicitudService, useValue: solicitudServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Recibir);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deberia cargar animales', () => {

    expect(animalServiceMock.getAll).toHaveBeenCalled();
    expect(component.animales.length).toBe(1);
    expect(component.animalesFiltrados.length).toBe(1);
  });

  it('deberia enviar solicitud de adopcion', () => {

    component.adoptar(1);

    expect(solicitudServiceMock.crear).toHaveBeenCalledWith(1);
    expect(component.exito)
      .toBe('¡Solicitud enviada! El admin revisará tu solicitud pronto.');
  });

  it('deberia volver al usuario', () => {

    component.volver();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/usuario']);
  });

});
