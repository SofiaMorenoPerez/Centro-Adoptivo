import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing-module';
import { FormsModule } from '@angular/forms';
import { App } from './app';
import { Inicio } from './inicio/inicio';
import { Login } from './login/login';
import { SignUp } from './signup/signup';
import { Usuario } from './usuario/usuario';
import { Dar } from './dar/dar';
import { Recibir } from './recibir/recibir';
import { Admin } from './admin/admin';

@NgModule({
  declarations: [App, Inicio, Login, SignUp, Usuario, Dar, Recibir, Admin],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners()],
  bootstrap: [App],
})
export class AppModule {}
