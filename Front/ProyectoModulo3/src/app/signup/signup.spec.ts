import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { SignUp } from './signup';
import { AuthService } from '../services/auth.service';

describe('SignUp', () => {

  let component: SignUp;
  let fixture: ComponentFixture<SignUp>;

  let authServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {

    authServiceMock = {
      register: vi.fn()
    };

    routerMock = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [SignUp],
      imports: [FormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SignUp);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debe mostrar error si username está vacío', () => {

    component.username = '';
    component.registrar();

    expect(component.error)
      .toBe('El usuario es obligatorio');
  });

  it('debe mostrar error si password está vacía', () => {

    component.username = 'isa';
    component.password = '';

    component.registrar();

    expect(component.error)
      .toBe('La contraseña es obligatoria');
  });

  it('debe mostrar error si las contraseñas no coinciden', () => {

    component.username = 'isa';
    component.password = '123';
    component.confirmarPassword = '456';

    component.registrar();

    expect(component.error)
      .toBe('Las contraseñas no coinciden');
  });

  it('debe mostrar error si el nombre está vacío', () => {

    component.username = 'isa';
    component.password = '123';
    component.confirmarPassword = '123';
    component.fullName = '';

    component.registrar();

    expect(component.error)
      .toBe('El nombre es obligatorio');
  });

  it('debe registrar usuario correctamente', () => {

    authServiceMock.register.mockReturnValue(
      of('Usuario registrado')
    );

    component.username = 'isa';
    component.password = '123';
    component.confirmarPassword = '123';
    component.fullName = 'Isabella';
    component.email = 'isa@gmail.com';
    component.phone = '300123';
    component.city = 'Bogotá';
    component.address = 'Calle 1';
    component.age = 20;

    component.registrar();

    expect(authServiceMock.register)
      .toHaveBeenCalled();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/login']);
  });

  it('debe mostrar error si register falla', () => {

    authServiceMock.register.mockReturnValue(
      throwError(() => ({
        error: 'Usuario ya existe'
      }))
    );

    component.username = 'isa';
    component.password = '123';
    component.confirmarPassword = '123';
    component.fullName = 'Isabella';
    component.email = 'isa@gmail.com';
    component.phone = '300123';
    component.city = 'Bogotá';
    component.address = 'Calle 1';
    component.age = 20;

    component.registrar();

    expect(component.error)
      .toBe('Usuario ya existe');
  });

  it('debe navegar al login', () => {

    component.irLogin();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/login']);
  });

});
