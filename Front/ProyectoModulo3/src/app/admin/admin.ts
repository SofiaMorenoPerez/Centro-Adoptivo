import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { AnimalService } from '../services/animal.service';
import { SolicitudService } from '../services/solicitud.service';
import { UserModel } from '../models/user.model';
import { AnimalModel } from '../models/animal.model';
import { SolicitudModel } from '../models/solicitud.model';


@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {
  vista = 'transacciones';

  usuarios: UserModel[] = [];
  admins: UserModel[] = [];
  animales: AnimalModel[] = [];
  solicitudes: SolicitudModel[] = [];

  cargando = false;
  error = '';
  exito = '';

  // Modal rechazar solicitud
  modalRechazar = false;
  solicitudSeleccionada: number | null = null;
  razonRechazo = '';

  // Modal crear admin
  modalCrear = false;
  nuevoUsername = '';
  nuevoPassword = '';
  nuevoFullName = '';
  nuevoEmail = '';
  nuevoPhone = '';
  nuevoCity = '';
  nuevoAddress = '';
  nuevoAge = 0;

  // Modal editar admin
  modalEditar = false;
  editarId = 0;
  editarUsername = '';
  editarPassword = '';
  editarFullName = '';
  editarEmail = '';
  editarPhone = '';
  editarCity = '';
  editarAddress = '';
  editarAge = 0;

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private animalService: AnimalService,
    private solicitudService: SolicitudService,
  ) {}

  ngOnInit(): void {
    this.cargarVista('transacciones');
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
      default:              break;
    }
  }

  // ─── TRANSACCIONES ─

  cargarSolicitudes(): void {
    this.cargando = true;
    this.solicitudService.getPendientes().subscribe({
      next: (data) => { this.solicitudes = data ?? []; this.cargando = false; },
      error: () => { this.error = 'Error al cargar las solicitudes.'; this.cargando = false; }
    });
  }

  aprobar(id: number): void {
    this.solicitudService.aprobar(id).subscribe({
      next: () => { this.exito = 'Solicitud aprobada exitosamente.'; this.cargarSolicitudes(); },
      error: (err) => { this.error = err.error || 'Error al aprobar la solicitud.'; }
    });
  }

  abrirModalRechazar(id: number): void {
    this.solicitudSeleccionada = id;
    this.razonRechazo = '';
    this.modalRechazar = true;
  }

  confirmarRechazo(): void {
    if (!this.razonRechazo.trim()) { this.error = 'Debes ingresar una razón de rechazo.'; return; }
    if (this.solicitudSeleccionada === null) { this.error = 'No hay solicitud seleccionada.'; return; }
    this.solicitudService.rechazar(this.solicitudSeleccionada, this.razonRechazo).subscribe({
      next: () => {
        this.exito = 'Solicitud rechazada exitosamente.';
        this.modalRechazar = false;
        this.solicitudSeleccionada = null;
        this.razonRechazo = '';
        this.cargarSolicitudes();
      },
      error: (err) => { this.error = err.error || 'Error al rechazar la solicitud.'; }
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
        setTimeout(() => {
          this.usuarios = (data || []).filter(u => u.role === 'USER');
          this.cargando = false;
        }, 0);
      },
      error: () => {
        this.error = 'Error al cargar los usuarios.';
        this.cargando = false;
      }
    });
  }

  eliminarUsuario(id: number): void {
    this.userService.delete(id).subscribe({
      next: () => { this.exito = 'Usuario eliminado exitosamente.'; this.cargarUsuarios(); },
      error: () => { this.error = 'Error al eliminar el usuario.'; }
    });
  }

  // ─── ANIMALES ───────────────────────────────────────────────────────────────

  cargarAnimales(): void {
    this.cargando = true;
    this.animalService.getAllAdmin().subscribe({
      next: (data) => {
        setTimeout(() => {
          this.animales = data || [];
          this.cargando = false;
        }, 0);
      },
      error: () => {
        this.error = 'Error al cargar los animales.';
        this.cargando = false;
      }
    });
  }

  eliminarAnimal(id: number): void {
    this.animalService.delete(id).subscribe({
      next: () => { this.exito = 'Animal eliminado exitosamente.'; this.cargarAnimales(); },
      error: () => { this.error = 'Error al eliminar el animal.'; }
    });
  }

  // ─── ADMINS ─────────────────────────────────────────────────────────────────

  cargarAdmins(): void {
    this.cargando = true;
    this.userService.getAll().subscribe({
      next: (data) => {
        setTimeout(() => {
          this.admins = (data || []).filter(u => u.role === 'ADMIN');
          this.cargando = false;
        }, 0);
      },
      error: () => {
        this.error = 'Error al cargar los administradores.';
        this.cargando = false;
      }
    });
  }

  eliminarAdmin(id: number): void {
    this.userService.delete(id).subscribe({
      next: () => { this.exito = 'Administrador eliminado exitosamente.'; this.cargarAdmins(); },
      error: () => { this.error = 'Error al eliminar el administrador.'; }
    });
  }

  // Modal crear admin
  abrirModalCrear(): void {
    this.nuevoUsername = '';
    this.nuevoPassword = '';
    this.nuevoFullName = '';
    this.nuevoEmail = '';
    this.nuevoPhone = '';
    this.nuevoCity = '';
    this.nuevoAddress = '';
    this.nuevoAge = 0;
    this.error = '';
    this.modalCrear = true;
  }

  cerrarModalCrear(): void {
    this.modalCrear = false;
  }

  crearAdmin(): void {
    this.authService.register(
      this.nuevoUsername,
      this.nuevoPassword,
      this.nuevoFullName,
      this.nuevoEmail,
      this.nuevoPhone,
      this.nuevoCity,
      this.nuevoAddress,
      this.nuevoAge
    ).subscribe({
      next: () => {
        this.userService.getAll().subscribe({
          next: (data) => {
            const creado = (data || []).find(u => u.username === this.nuevoUsername);
            if (creado) {
              this.userService.cambiarRol(creado.id, 'ADMIN').subscribe({
                next: () => {
                  this.exito = 'Administrador creado exitosamente.';
                  this.modalCrear = false;
                  this.cargarAdmins();
                },
                error: () => { this.error = 'Error al asignar rol ADMIN.'; }
              });
            }
          },
          error: () => { this.error = 'Error al buscar el usuario creado.'; }
        });
      },
      error: (err) => {
        this.error = typeof err.error === 'string' ? err.error : 'Error al crear el administrador.';
      }
    });
  }

  // Modal editar admin
  abrirModalEditar(admin: UserModel): void {
    this.editarId = admin.id;
    this.editarUsername = admin.username;
    this.editarPassword = '';
    this.editarFullName = admin.fullName;
    this.editarEmail = admin.email;
    this.editarPhone = admin.phone;
    this.editarCity = admin.city;
    this.editarAddress = admin.address;
    this.editarAge = admin.age;
    this.error = '';
    this.modalEditar = true;
  }

  cerrarModalEditar(): void {
    this.modalEditar = false;
  }

  guardarEdicionAdmin(): void {
    const actualizado: UserModel = {
      id: this.editarId,
      username: this.editarUsername,
      password: this.editarPassword || undefined,
      fullName: this.editarFullName,
      email: this.editarEmail,
      phone: this.editarPhone,
      city: this.editarCity,
      address: this.editarAddress,
      age: this.editarAge,
      role: 'ADMIN'
    };
    this.userService.updateAdmin(this.editarId, actualizado).subscribe({
      next: () => {
        this.exito = 'Administrador actualizado exitosamente.';
        this.modalEditar = false;
        this.cargarAdmins();
      },
      error: (err) => {
        this.error = typeof err.error === 'string' ? err.error : 'Error al actualizar el administrador.';
      }
    });
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/inicio']);
  }
}
