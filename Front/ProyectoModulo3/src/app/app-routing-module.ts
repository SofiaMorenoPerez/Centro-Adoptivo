import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Inicio } from './inicio/inicio';
import { Login } from './login/login';
import { SignUp } from './signup/signup';
import { Usuario } from './usuario/usuario';
import { Dar } from './dar/dar';
import { Recibir } from './recibir/recibir';
import { Admin } from './admin/admin';
import { Perfil } from './perfil/perfil';
import { AuthGuard } from './guard/auth.guard';
import { RoleGuard } from './guard/role.guard';

const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },

  // Rutas públicas — sin guards
  { path: 'inicio', component: Inicio },
  { path: 'login', component: Login },
  { path: 'sign-up', component: SignUp },

  // Rutas de USER — necesita token y rol USER
  {
    path: 'usuario',
    component: Usuario,
    canActivate: [AuthGuard, RoleGuard],
    data: { rol: 'USER' }
  },
  {
    path: 'dar',
    component: Dar,
    canActivate: [AuthGuard, RoleGuard],
    data: { rol: 'USER' }
  },
  {
    path: 'recibir',
    component: Recibir,
    canActivate: [AuthGuard, RoleGuard],
    data: { rol: 'USER' }
  },
  {
    path: 'perfil',
    component: Perfil,
    canActivate: [AuthGuard, RoleGuard],
    data: { rol: 'USER' }
  },

  // Rutas de ADMIN — necesita token y rol ADMIN
  {
    path: 'admin',
    component: Admin,
    canActivate: [AuthGuard, RoleGuard],
    data: { rol: 'ADMIN' }
  },

  // Ruta no encontrada
  { path: '**', redirectTo: 'inicio' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
// skipcq: JS-0327
export class AppRoutingModule {}
