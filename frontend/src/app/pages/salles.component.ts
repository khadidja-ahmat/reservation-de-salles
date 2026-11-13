import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { Reservation, Salle } from '../core/models';

@Component({
  selector: 'app-salles',
  standalone: true,
  imports: [FormsModule, DatePipe],
  template: `
    <h2>Salles disponibles</h2>
    @if (message()) { <p class="info">{{ message() }}</p> }
    @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }

    <div class="grille">
      @for (s of salles(); track s.id) {
        <div class="carte">
          <h3>{{ s.nom }}</h3>
          <p>👥 {{ s.capacite }} places</p>
          <p class="gris">{{ s.equipements || 'Aucun équipement' }}</p>
          <button class="principal" (click)="choisir(s)">Réserver</button>
          @if (auth.estAdmin()) {
            <button class="danger" (click)="supprimer(s)">Supprimer</button>
          }
        </div>
      }
    </div>

    @if (choisie()) {
      <div class="carte formulaire">
        <h3>Réserver : {{ choisie()!.nom }}</h3>

        @if (creneaux().length > 0) {
          <p class="gris">Déjà réservé :</p>
          <ul>
            @for (c of creneaux(); track c.id) {
              <li>{{ c.debut | date: 'dd/MM HH:mm' }} → {{ c.fin | date: 'HH:mm' }}</li>
            }
          </ul>
        }

        <form (ngSubmit)="reserver()">
          <label>Début <input name="debut" type="datetime-local" [(ngModel)]="debut" required /></label>
          <label>Fin <input name="fin" type="datetime-local" [(ngModel)]="fin" required /></label>
          <label>Motif <input name="motif" [(ngModel)]="motif" /></label>
          <button class="principal" type="submit">Confirmer</button>
          <button type="button" (click)="choisie.set(null)">Fermer</button>
        </form>
      </div>
    }

    @if (auth.estAdmin()) {
      <div class="carte formulaire">
        <h3>Ajouter une salle (admin)</h3>
        <form (ngSubmit)="ajouterSalle()">
          <label>Nom <input name="n" [(ngModel)]="nouvelle.nom" required /></label>
          <label>Capacité <input name="c" type="number" min="1" [(ngModel)]="nouvelle.capacite" required /></label>
          <label>Équipements <input name="e" [(ngModel)]="nouvelle.equipements" /></label>
          <button class="principal" type="submit">Ajouter</button>
        </form>
      </div>
    }
  `,
})
export class SallesComponent implements OnInit {
  salles = signal<Salle[]>([]);
  choisie = signal<Salle | null>(null);
  creneaux = signal<Reservation[]>([]);
  message = signal('');
  erreur = signal('');

  debut = '';
  fin = '';
  motif = '';
  nouvelle = { nom: '', capacite: 10, equipements: '' };

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.charger();
  }

  charger() {
    this.api.salles().subscribe((s) => this.salles.set(s));
  }

  choisir(s: Salle) {
    this.choisie.set(s);
    this.message.set('');
    this.erreur.set('');
    this.api.creneaux(s.id).subscribe((c) => this.creneaux.set(c));
  }

  reserver() {
    const salle = this.choisie();
    if (!salle) return;
    this.erreur.set('');
    this.api
      .reserver({ salleId: salle.id, debut: this.debut, fin: this.fin, motif: this.motif })
      .subscribe({
        next: () => {
          this.message.set('Réservation confirmée ✔');
          this.choisie.set(null);
          this.debut = this.fin = this.motif = '';
        },
        error: (e) => this.erreur.set(e.error?.message ?? 'Erreur de réservation'),
      });
  }

  ajouterSalle() {
    this.api.creerSalle(this.nouvelle).subscribe({
      next: () => {
        this.nouvelle = { nom: '', capacite: 10, equipements: '' };
        this.charger();
      },
      error: (e) => this.erreur.set(e.error?.message ?? 'Erreur'),
    });
  }

  supprimer(s: Salle) {
    this.api.supprimerSalle(s.id).subscribe({
      next: () => this.charger(),
      error: (e) => this.erreur.set(e.error?.message ?? 'Erreur'),
    });
  }
}
