import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Inicio } from './inicio/inicio';
import { Login } from './login/login';
import { SignUp } from './signup/signup';
import { Usuario } from './usuario/usuario';
import { Dar } from './dar/dar';
import { Recibir } from './recibir/recibir';
import { Admin } from './admin/admin';


const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: 'inicio', component: Inicio },
  { path: 'login', component: Login },
  { path: 'sign-up', component: SignUp },
  { path: 'usuario', component: Usuario },
  { path: 'dar', component: Dar },
  { path: 'recibir', component: Recibir },
  { path: 'admin', component: Admin },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
