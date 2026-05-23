import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Inicio } from './inicio/inicio';
import { Login } from './login/login';
import { SignUp } from './signup/signup';
import { Usuario } from './usuario/usuario';
import { Dar } from './dar/dar';
import { Recibir } from './recibir/recibir';
import { Admin } from './admin/admin';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { Perfil } from './perfil/perfil';

@NgModule({
  declarations: [App, Inicio, Login, SignUp, Usuario, Dar, Recibir, Admin, Perfil],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient()],
  bootstrap: [App],
})
// skipcq: JS-0327
export class AppModule {}
