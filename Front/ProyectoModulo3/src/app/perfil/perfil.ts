import { Component, OnInit } from '@angular/core';
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
  username: string = '';
  fullName: string = '';
  email: string = '';
  phone: string = '';
  city: string = '';
  address: string = '';
  age: number = 0;
  editando: boolean = false;
  error: string = '';
  exito: string = '';

  constructor(
    private router: Router,
    private userService: UserService,
    private authService: AuthService
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
      },
      error: (err) => {
        this.error = err.error;
        this.exito = '';
      }
    });
  }

  eliminarCuenta(): void {
    const id = this.authService.getId();
    this.userService.delete(id).subscribe({
      next: () => {
        this.authService.logout();
        this.router.navigate(['/inicio']);
      },
      error: () => {
        this.error = 'Error al eliminar la cuenta';
      }
    });
  }

  volver(): void {
    this.router.navigate(['/usuario']);
  }
}
