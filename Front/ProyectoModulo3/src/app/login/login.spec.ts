import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Login } from './login';
import { AuthService } from '../services/auth.service';

describe('Login', () => {

  let component: Login;
  let fixture: ComponentFixture<Login>;

  let authServiceMock: { login: ReturnType<typeof vi.fn>; guardarToken: ReturnType<typeof vi.fn>; guardarRol: ReturnType<typeof vi.fn>; guardarId: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {

    authServiceMock = {
      login: vi.fn(),
      guardarToken: vi.fn(),
      guardarRol: vi.fn(),
      guardarId: vi.fn()
    };

    routerMock = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [Login],
      imports: [FormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debe mostrar error si usuario está vacío', () => {

    component.usuario = '';
    component.contrasena = '123';

    component.ingresar();

    expect(component.error)
      .toBe('El usuario es obligatorio');
  });

  it('debe mostrar error si contraseña está vacía', () => {

    component.usuario = 'isa';
    component.contrasena = '';

    component.ingresar();

    expect(component.error)
      .toBe('La contraseña es obligatoria');
  });

  it('debe iniciar sesión y redirigir a usuario', () => {

    authServiceMock.login.mockReturnValue(
      of({
        token: 'abc123',
        role: 'USER',
        id: 9
      })
    );

    component.usuario = 'isa';
    component.contrasena = '123';

    component.ingresar();

    expect(authServiceMock.login)
      .toHaveBeenCalledWith('isa', '123');

    expect(authServiceMock.guardarToken)
      .toHaveBeenCalledWith('abc123');

    expect(authServiceMock.guardarRol)
      .toHaveBeenCalledWith('USER');

    expect(authServiceMock.guardarId)
      .toHaveBeenCalledWith(9);

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/usuario']);
  });

  it('debe redirigir a admin si el rol es ADMIN', () => {

    authServiceMock.login.mockReturnValue(
      of({
        token: 'admin123',
        role: 'ADMIN',
        id: 1
      })
    );

    component.usuario = 'admin';
    component.contrasena = '123';

    component.ingresar();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/admin']);
  });

  it('debe mostrar error si login falla', () => {

    authServiceMock.login.mockReturnValue(
      throwError(() => ({
        error: 'Credenciales incorrectas'
      }))
    );

    component.usuario = 'isa';
    component.contrasena = 'wrong';

    component.ingresar();

    expect(component.error)
      .toBe('Credenciales incorrectas');
  });

});
