import { FormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';
import { of } from 'rxjs';

import { Usuario } from './usuario';
import { AuthService } from '../services/auth.service';
import { NotificacionService } from '../services/notificacion.service';

describe('Usuario', () => {

  let component: Usuario;
  let fixture: ComponentFixture<Usuario>;

  let authServiceMock: { logout: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };
  let notificacionServiceMock: { getMis: ReturnType<typeof vi.fn>; marcarTodasLeidas: ReturnType<typeof vi.fn> };

  beforeEach(async () => {

    authServiceMock = {
      logout: vi.fn()
    };

    routerMock = {
      navigate: vi.fn()
    };

    notificacionServiceMock = {
      getMis: vi.fn().mockReturnValue(of([])),
      marcarTodasLeidas: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      declarations: [Usuario],
      imports: [FormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: NotificacionService, useValue: notificacionServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Usuario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debe abrir y cerrar el menú perfil', () => {

    component.menuPerfil = false;

    component.toggleMenu();

    expect(component.menuPerfil).toBe(true);

    component.toggleMenu();

    expect(component.menuPerfil).toBe(false);
  });

  it('debe navegar a dar', () => {

    component.irDar();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/dar']);
  });

  it('debe navegar a recibir', () => {

    component.irRecibir();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/recibir']);
  });

  it('debe navegar a perfil', () => {

    component.irPerfil();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/perfil']);
  });

  it('debe cerrar sesión', () => {

    component.cerrarSesion();

    expect(authServiceMock.logout).toHaveBeenCalled();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/inicio']);
  });

});
