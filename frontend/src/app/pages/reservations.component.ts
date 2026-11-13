import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { Reservation } from '../core/models';

@Component({
  selector: 'app-reservations',
  standalone: true,
  imports: [DatePipe],
  template: `
    <h2>{{ auth.estAdmin() ? 'Toutes les réservations' : 'Mes réservations' }}</h2>

    @if (liste().length === 0) {
      <p class="gris">Aucune réservation pour le moment.</p>
    }

    <table>
      @for (r of liste(); track r.id) {
        <tr [class.annulee]="r.statut === 'ANNULEE'">
          <td><strong>{{ r.salleNom }}</strong>
            @if (auth.estAdmin()) { <span class="gris"> · {{ r.utilisateurNom }}</span> }
          </td>
          <td>{{ r.debut | date: 'dd/MM/yyyy HH:mm' }} → {{ r.fin | date: 'HH:mm' }}</td>
          <td>{{ r.motif }}</td>
          <td>
            @if (r.statut === 'CONFIRMEE') {
              <button class="danger" (click)="annuler(r)">Annuler</button>
            } @else {
              Annulée
            }
          </td>
        </tr>
      }
    </table>
  `,
})
export class ReservationsComponent implements OnInit {
  liste = signal<Reservation[]>([]);

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.charger();
  }

  charger() {
    this.api.reservations().subscribe((l) => this.liste.set(l));
  }

  annuler(r: Reservation) {
    this.api.annuler(r.id).subscribe(() => this.charger());
  }
}
