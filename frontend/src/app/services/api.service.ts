import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Reservation, Salle } from '../core/models';

/** Tous les appels vers l'API REST (salles et réservations). */
@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  salles() {
    return this.http.get<Salle[]>('/api/salles');
  }

  creneaux(salleId: number) {
    return this.http.get<Reservation[]>(`/api/salles/${salleId}/creneaux`);
  }

  creerSalle(s: { nom: string; capacite: number; equipements: string }) {
    return this.http.post<Salle>('/api/salles', s);
  }

  supprimerSalle(id: number) {
    return this.http.delete<void>(`/api/salles/${id}`);
  }

  reservations() {
    return this.http.get<Reservation[]>('/api/reservations');
  }

  reserver(r: { salleId: number; debut: string; fin: string; motif: string }) {
    return this.http.post<Reservation>('/api/reservations', r);
  }

  annuler(id: number) {
    return this.http.delete<void>(`/api/reservations/${id}`);
  }
}
