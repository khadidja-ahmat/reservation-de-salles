import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-connexion',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="carte petite">
      <h2>{{ modeInscription() ? 'Créer un compte' : 'Connexion' }}</h2>

      <form (ngSubmit)="valider()">
        @if (modeInscription()) {
          <label>Nom
            <input name="nom" [(ngModel)]="nom" required />
          </label>
        }
        <label>Email
          <input name="email" type="email" [(ngModel)]="email" required />
        </label>
        <label>Mot de passe
          <input name="mdp" type="password" [(ngModel)]="motDePasse" required minlength="6" />
        </label>

        @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }

        <button class="principal" type="submit">
          {{ modeInscription() ? "S'inscrire" : 'Se connecter' }}
        </button>
      </form>

      <p class="lien" (click)="modeInscription.set(!modeInscription())">
        {{ modeInscription() ? 'Déjà un compte ? Se connecter' : 'Pas de compte ? Créer un compte' }}
      </p>
    </div>
  `,
})
export class ConnexionComponent {
  modeInscription = signal(false);
  erreur = signal('');
  nom = '';
  email = '';
  motDePasse = '';

  constructor(private auth: AuthService, private router: Router) {}

  valider() {
    this.erreur.set('');
    const appel = this.modeInscription()
      ? this.auth.inscription(this.nom, this.email, this.motDePasse)
      : this.auth.connexion(this.email, this.motDePasse);

    appel.subscribe({
      next: () => this.router.navigate(['/salles']),
      error: (e) => this.erreur.set(e.error?.message ?? 'Erreur, réessayez'),
    });
  }
}
