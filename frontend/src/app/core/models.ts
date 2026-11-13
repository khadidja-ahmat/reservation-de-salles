// Les objets échangés avec l'API Spring Boot

export type Role = 'USER' | 'ADMIN';

export interface Session {
  token: string;
  nom: string;
  email: string;
  role: Role;
}

export interface Salle {
  id: number;
  nom: string;
  capacite: number;
  equipements: string;
}

export interface Reservation {
  id: number;
  salleId: number;
  salleNom: string;
  utilisateurNom: string;
  debut: string;
  fin: string;
  motif: string;
  statut: 'CONFIRMEE' | 'ANNULEE';
}
