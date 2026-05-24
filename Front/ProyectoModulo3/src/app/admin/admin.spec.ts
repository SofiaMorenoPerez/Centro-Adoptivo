import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { Admin } from './admin';

import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { AnimalService } from '../services/animal.service';
import { SolicitudService } from '../services/solicitud.service';

describe('Admin', () => {

  let component: Admin;
  let fixture: ComponentFixture<Admin>;

  let routerMock: { navigate: ReturnType<typeof vi.fn> };
  let authServiceMock: { logout: ReturnType<typeof vi.fn>; register: ReturnType<typeof vi.fn> };
  let userServiceMock: { getAll: ReturnType<typeof vi.fn>; delete: ReturnType<typeof vi.fn>; cambiarRol: ReturnType<typeof vi.fn>; updateAdmin: ReturnType<typeof vi.fn> };
  let animalServiceMock: { getAllAdmin: ReturnType<typeof vi.fn>; delete: ReturnType<typeof vi.fn> };
  let solicitudServiceMock: { getPendientes: ReturnType<typeof vi.fn>; aprobar: ReturnType<typeof vi.fn>; rechazar: ReturnType<typeof vi.fn> };

  // ... resto del archivo sin cambios

  beforeEach(async () => {

    routerMock = {
      navigate: vi.fn()
    };

    authServiceMock = {
      logout: vi.fn(),
      register: vi.fn().mockReturnValue(of({}))
    };

    userServiceMock = {
      getAll: vi.fn().mockReturnValue(of([])),
      delete: vi.fn().mockReturnValue(of({})),
      cambiarRol: vi.fn().mockReturnValue(of({})),
      updateAdmin: vi.fn().mockReturnValue(of({}))
    };

    animalServiceMock = {
      getAllAdmin: vi.fn().mockReturnValue(of([])),
      delete: vi.fn().mockReturnValue(of({}))
    };

    solicitudServiceMock = {
      getPendientes: vi.fn().mockReturnValue(of([])),
      aprobar: vi.fn().mockReturnValue(of({})),
      rechazar: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      declarations: [Admin],
      imports: [FormsModule],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: UserService, useValue: userServiceMock },
        { provide: AnimalService, useValue: animalServiceMock },
        { provide: SolicitudService, useValue: solicitudServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Admin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debe cambiar la vista', () => {

    component.cambiarVista('usuarios');

    expect(component.vista).toBe('usuarios');
  });

  it('debe abrir modal crear', () => {

    component.abrirModalCrear();

    expect(component.modalCrear).toBe(true);
  });

  it('debe cerrar modal crear', () => {

    component.modalCrear = true;

    component.cerrarModalCrear();

    expect(component.modalCrear).toBe(false);
  });

  it('debe abrir modal rechazar', () => {

    component.abrirModalRechazar(1);

    expect(component.modalRechazar).toBe(true);

    expect(component.solicitudSeleccionada).toBe(1);
  });

  it('debe cancelar rechazo', () => {

    component.modalRechazar = true;

    component.cancelarRechazo();

    expect(component.modalRechazar).toBe(false);

    expect(component.solicitudSeleccionada).toBeNull();
  });

  it('debe navegar al cerrar sesión', () => {

    component.cerrarSesion();

    expect(authServiceMock.logout).toHaveBeenCalled();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/inicio']);
  });

  it('debe aprobar solicitud', () => {

    component.aprobar(1);

    expect(solicitudServiceMock.aprobar).toHaveBeenCalledWith(1);
  });

  it('debe eliminar usuario', () => {

    component.eliminarUsuario(1);

    expect(userServiceMock.delete).toHaveBeenCalledWith(1);
  });

  it('debe eliminar animal', () => {

    component.eliminarAnimal(1);

    expect(animalServiceMock.delete).toHaveBeenCalledWith(1);
  });

});
