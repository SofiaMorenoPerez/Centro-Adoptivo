import { inject, Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const rolRequerido = route.data['rol'];
    const rolActual = this.authService.getRol();

    if (rolActual === rolRequerido) {
      return true;
    }

    // Si tiene token pero rol incorrecto, lo mandamos a su vista
    if (rolActual === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/usuario']);
    }
    return false;
  }
}
