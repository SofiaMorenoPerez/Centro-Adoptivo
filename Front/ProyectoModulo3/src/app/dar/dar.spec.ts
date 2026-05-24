import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Dar } from './dar';
import { AnimalService } from '../services/animal.service';

describe('Dar', () => {

  let component: Dar;
  let fixture: ComponentFixture<Dar>;

  let animalServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {

    animalServiceMock = {
      registrar: vi.fn().mockReturnValue(of({}))
    };

    routerMock = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [Dar],
      imports: [FormsModule],
      providers: [
        { provide: AnimalService, useValue: animalServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Dar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deberia mostrar error si no hay imagen', () => {

    component.guardar();

    expect(component.error).toBe('La foto del animal es obligatoria.');
  });

  it('deberia mostrar error si el nombre esta vacio', () => {

    component.imagenArchivo = new File([''], 'foto.png');

    component.guardar();

    expect(component.error).toBe('El nombre del animal es obligatorio.');
  });

  it('deberia mostrar error si las observaciones son cortas', () => {

    component.imagenArchivo = new File([''], 'foto.png');
    component.nombre = 'Max';
    component.observaciones = 'hola';

    component.guardar();

    expect(component.error)
      .toBe('Las observaciones deben tener mínimo 10 caracteres.');
  });

  it('deberia registrar el animal', () => {

    component.imagenArchivo = new File([''], 'foto.png');
    component.nombre = 'Max';
    component.observaciones = 'Muy amigable y jugueton';

    component.guardar();

    expect(animalServiceMock.registrar).toHaveBeenCalled();
  });

  it('deberia volver al usuario', () => {

    component.volver();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/usuario']);
  });

});
