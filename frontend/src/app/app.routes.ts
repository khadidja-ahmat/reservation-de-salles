import { Routes } from '@angular/router';
import { ConnexionComponent } from './pages/connexion.component';
import { SallesComponent } from './pages/salles.component';
import { ReservationsComponent } from './pages/reservations.component';
import { connecteGuard } from './core/guards';

export const routes: Routes = [
  { path: 'connexion', component: ConnexionComponent },
  { path: 'salles', component: SallesComponent, canActivate: [connecteGuard] },
  { path: 'reservations', component: ReservationsComponent, canActivate: [connecteGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'salles' },
  { path: '**', redirectTo: 'salles' },
];
