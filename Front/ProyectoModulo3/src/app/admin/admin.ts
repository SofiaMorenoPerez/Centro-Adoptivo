import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { AnimalService } from '../services/animal.service';
import { SolicitudService } from '../services/solicitud.service';
import { NotificacionService } from '../services/notificacion.service';
import { UserModel } from '../models/user.model';
import { AnimalModel } from '../models/animal.model';
import { SolicitudModel } from '../models/solicitud.model';
import { NotificacionModel } from '../models/notificacion.model';

@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {
  vista: string = 'transacciones';
  menuCampana: boolean = false;

  usuarios: UserModel[] = [];
  admins: UserModel[] = [];
  animales: AnimalModel[] = [];
  solicitudes: SolicitudModel[] = [];
  notificaciones: NotificacionModel[] = [];
  cantidadNoLeidas: number = 0;

  // Estados
  cargando: boolean = false;
  error: string = '';
  exito: string = '';


  modalRechazar: boolean = false;
  solicitudSeleccionada: number | null = null;
  razonRechazo: string = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private animalService: AnimalService,
    private solicitudService: SolicitudService,
    private notificacionService: NotificacionService
  ) {}

  ngOnInit(): void {
    this.cargarVista('transacciones');
    this.cargarNotificaciones();
  }


  cambiarVista(v: string): void {
    this.vista = v;
    this.error = '';
    this.exito = '';
    this.cargarVista(v);
  }

  cargarVista(v: string): void {
    switch (v) {
      case 'transacciones': this.cargarSolicitudes(); break;
      case 'usuarios':      this.cargarUsuarios();    break;
      case 'animales':      this.cargarAnimales();    break;
      case 'admins':        this.cargarAdmins();      break;
    }
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


  cargarSolicitudes(): void {
    this.cargando = true;
    this.solicitudService.getPendientes().subscribe({
      next: (data) => {
        this.solicitudes = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'Error al cargar las solicitudes.';
        this.cargando = false;
      }
    });
  }

  aprobar(id: number): void {
    this.solicitudService.aprobar(id).subscribe({
      next: () => {
        this.exito = 'Solicitud aprobada exitosamente.';
        this.cargarSolicitudes();
      },
      error: (err) => {
        this.error = err.error || 'Error al aprobar la solicitud.';
      }
    });
  }

  abrirModalRechazar(id: number): void {
    this.solicitudSeleccionada = id;
    this.razonRechazo = '';
    this.modalRechazar = true;
  }

  confirmarRechazo(): void {
    if (!this.razonRechazo.trim()) {
      this.error = 'Debes ingresar una razón de rechazo.';
      return;
    }
    this.solicitudService.rechazar(this.solicitudSeleccionada!, this.razonRechazo).subscribe({
      next: () => {
        this.exito = 'Solicitud rechazada exitosamente.';
        this.modalRechazar = false;
        this.solicitudSeleccionada = null;
        this.razonRechazo = '';
        this.cargarSolicitudes();
      },
      error: (err) => {
        this.error = err.error || 'Error al rechazar la solicitud.';
      }
    });
  }

  cancelarRechazo(): void {
    this.modalRechazar = false;
    this.solicitudSeleccionada = null;
    this.razonRechazo = '';
  }

  // ─── USUARIOS ───────────────────────────────────────────────────────────────

  cargarUsuarios(): void {
    this.cargando = true;
    this.userService.getAll().subscribe({
      next: (data) => {
        this.usuarios = data.filter(u => u.role === 'USER');
        this.cargando = false;
      },
      error: () => {
        this.error = 'Error al cargar los usuarios.';
        this.cargando = false;
      }
    });
  }

  eliminarUsuario(id: number): void {
    this.userService.delete(id).subscribe({
      next: () => {
        this.exito = 'Usuario eliminado exitosamente.';
        this.cargarUsuarios();
      },
      error: () => {
        this.error = 'Error al eliminar el usuario.';
      }
    });
  }


  cargarAnimales(): void {
    this.cargando = true;
    this.animalService.getAllAdmin().subscribe({
      next: (data) => {
        this.animales = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'Error al cargar los animales.';
        this.cargando = false;
      }
    });
  }

  eliminarAnimal(id: number): void {
    this.animalService.delete(id).subscribe({
      next: () => {
        this.exito = 'Animal eliminado exitosamente.';
        this.cargarAnimales();
      },
      error: () => {
        this.error = 'Error al eliminar el animal.';
      }
    });
  }


  cargarAdmins(): void {
    this.cargando = true;
    this.userService.getAll().subscribe({
      next: (data) => {
        this.admins = data.filter(u => u.role === 'ADMIN');
        this.cargando = false;
      },
      error: () => {
        this.error = 'Error al cargar los administradores.';
        this.cargando = false;
      }
    });
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/inicio']);
  }
}
