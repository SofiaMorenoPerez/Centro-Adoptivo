import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Perfil } from './perfil';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';

describe('Perfil', () => {

  let component: Perfil;
  let fixture: ComponentFixture<Perfil>;

  let userServiceMock: any;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {

    userServiceMock = {
      getPerfil: vi.fn().mockReturnValue(of({
        id: 1,
        username: 'isa',
        fullName: 'Isabella',
        email: 'isa@test.com',
        phone: '123',
        city: 'Bogota',
        address: 'Calle 1',
        age: 20,
        role: 'USER'
      })),
      update: vi.fn().mockReturnValue(of({}))
    };

    authServiceMock = {
      getRol: vi.fn().mockReturnValue('USER')
    };

    routerMock = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [Perfil],
      imports: [FormsModule],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Perfil);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deberia cargar el perfil', () => {
    expect(userServiceMock.getPerfil).toHaveBeenCalled();
    expect(component.username).toBe('isa');
    expect(component.fullName).toBe('Isabella');
  });

  it('deberia activar la edicion', () => {
    component.editando = false;

    component.activarEdicion();

    expect(component.editando).toBe(true);
  });

  it('deberia guardar cambios', () => {

    component.id = 1;
    component.username = 'isa';
    component.fullName = 'Isabella';

    component.guardarCambios();

    expect(userServiceMock.update).toHaveBeenCalled();
    expect(component.exito).toBe('Perfil actualizado exitosamente');
  });

  it('deberia volver al usuario', () => {

    component.volver();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/usuario']);
  });

});
