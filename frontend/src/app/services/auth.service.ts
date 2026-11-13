import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { Session } from '../core/models';

const CLE = 'reservation-salles-session';

/** Gère la connexion : appelle l'API et garde le token dans le navigateur. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private session = signal<Session | null>(this.charger());

  readonly utilisateur = this.session.asReadonly();
  readonly connecte = computed(() => this.session() !== null);
  readonly estAdmin = computed(() => this.session()?.role === 'ADMIN');

  constructor(private http: HttpClient, private router: Router) {}

  connexion(email: string, motDePasse: string) {
    return this.http
      .post<Session>('/api/auth/connexion', { email, motDePasse })
      .pipe(tap((s) => this.enregistrer(s)));
  }

  inscription(nom: string, email: string, motDePasse: string) {
    return this.http
      .post<Session>('/api/auth/inscription', { nom, email, motDePasse })
      .pipe(tap((s) => this.enregistrer(s)));
  }

  deconnexion() {
    this.session.set(null);
    try { localStorage.removeItem(CLE); } catch { /* ignoré */ }
    this.router.navigate(['/connexion']);
  }

  token(): string | null {
    return this.session()?.token ?? null;
  }

  private enregistrer(s: Session) {
    this.session.set(s);
    try { localStorage.setItem(CLE, JSON.stringify(s)); } catch { /* ignoré */ }
  }

  private charger(): Session | null {
    try {
      const brut = localStorage.getItem(CLE);
      return brut ? (JSON.parse(brut) as Session) : null;
    } catch {
      return null;
    }
  }
}
