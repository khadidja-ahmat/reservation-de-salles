import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/** Ajoute automatiquement "Authorization: Bearer <token>" à chaque appel API. */
export const authInterceptor: HttpInterceptorFn = (requete, suivant) => {
  const token = inject(AuthService).token();
  if (token && requete.url.startsWith('/api')) {
    requete = requete.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return suivant(requete);
};
