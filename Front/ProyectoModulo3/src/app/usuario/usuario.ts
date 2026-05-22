import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificacionService } from '../services/notificacion.service';
import { NotificacionModel } from '../models/notificacion.model';

@Component({
  selector: 'app-usuario',
  standalone: false,
  templateUrl: './usuario.html',
  styleUrl: './usuario.css'
})
export class Usuario implements OnInit {
  menuPerfil: boolean = false;
  menuCampana: boolean = false;

  notificaciones: NotificacionModel[] = [];
  cantidadNoLeidas: number = 0;

  constructor(
    private router: Router,
    private authService: AuthService,
    private notificacionService: NotificacionService
  ) {}

  ngOnInit(): void {
    this.cargarNotificaciones();
  }

  cargarNotificaciones(): void {
    this.notificacionService.getMis().subscribe({
      next: (notifs) => {
        this.notificaciones = notifs;
        this.cantidadNoLeidas = notifs.filter(n => !n.leida).length;
      },
      error: () => {}
    });
  }

  toggleCampana(): void {
    this.menuCampana = !this.menuCampana;
    this.menuPerfil = false;

    // Al abrir la campana marcamos todas como leídas
    if (this.menuCampana && this.cantidadNoLeidas > 0) {
      this.notificacionService.marcarTodasLeidas().subscribe({
        next: () => {
          this.cantidadNoLeidas = 0;
          this.notificaciones.forEach(n => n.leida = true);
        },
        error: () => {}
      });
    }
  }

  toggleMenu(): void {
    this.menuPerfil = !this.menuPerfil;
    this.menuCampana = false;
  }

  irDar(): void {
    this.router.navigate(['/dar']);
  }

  irRecibir(): void {
    this.router.navigate(['/recibir']);
  }

  irPerfil(): void {
    this.menuPerfil = false;
    this.router.navigate(['/perfil']);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/inicio']);
  }
}
