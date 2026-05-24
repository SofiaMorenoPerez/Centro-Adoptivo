import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { UserModel } from '../models/user.model';

@Component({
  selector: 'app-perfil',
  standalone: false,
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class Perfil implements OnInit {

  id: number = 0;
  username = '';
  fullName = '';
  email = '';
  phone = '';
  city = '';
  address = '';
  age = 0;
  editando = true;
  error = '';
  exito = '';
  password = '';

  constructor(
    private router: Router,
    private userService: UserService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPerfil();
  }

  cargarPerfil(): void {
    this.userService.getPerfil().subscribe({
      next: (usuario) => {
        this.id = usuario.id;
        this.username = usuario.username;
        this.fullName = usuario.fullName;
        this.email = usuario.email;
        this.phone = usuario.phone;
        this.city = usuario.city;
        this.address = usuario.address;
        this.age = usuario.age;
        this.editando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Error al cargar el perfil';
      }
    });
  }

  activarEdicion(): void {
    this.editando = true;
    this.error = '';
    this.exito = '';
  }

  guardarCambios(): void {
    const usuario: UserModel = {
      id: this.id,
      username: this.username,
      fullName: this.fullName,
      password: this.password || undefined,
      email: this.email,
      phone: this.phone,
      city: this.city,
      address: this.address,
      age: this.age,
      role: this.authService.getRol() || ''
    };

    this.userService.update(usuario).subscribe({
      next: () => {
        this.editando = false;
        this.exito = 'Perfil actualizado exitosamente';
        this.error = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = typeof err.error === 'string'
          ? err.error
          : 'Error al actualizar el perfil';
        this.exito = '';
      }
    });
  }

  volver(): void {
    this.router.navigate(['/usuario']);
  }
}
