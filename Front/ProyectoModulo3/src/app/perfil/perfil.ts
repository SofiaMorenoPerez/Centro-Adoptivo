import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-perfil',
  standalone: false,
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class Perfil {

  username: string = 'isa123';
  fullName: string = 'Isabella Moreno';
  email: string = 'isa@gmail.com';
  phone: string = '3001234567';
  city: string = 'Bogotá';
  address: string = 'Calle 123';
  age: number = 20;
  editando: boolean = false;

  constructor(
    private router: Router,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarPerfil();
  }

  cargarPerfil() {
    this.userService.getPerfil().subscribe({

      next: (usuario) => {
        this.username = usuario.username;
        this.fullName = usuario.fullName;
        this.email = usuario.email;
        this.phone = usuario.phone;
        this.city = usuario.city;
        this.address = usuario.address;
        this.age = usuario.age;
      },

      error: (error) => {
        console.log(error);
      }
    });

  }

  activarEdicion() {
    this.editando = true;
  }

  guardarCambios() {
    this.editando = false;
  }

  eliminarCuenta() {
    this.authService.logout();
    this.router.navigate(['/inicio']);
  }

  volver() {
    this.router.navigate(['/usuario']);
  }
}
