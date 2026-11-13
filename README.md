# Réservation de salles

Application web de **réservation de salles** : on choisit une salle, un créneau, et l'application refuse automatiquement les créneaux déjà pris.

Projet personnel, réalisé pour mettre en pratique une architecture complète **Spring Boot + Angular + PostgreSQL + Docker**.

> Projet personnel réalisé en 2026, avec l'aide d'un assistant IA pour le développement.

## Fonctionnalités

- Inscription et connexion (authentification par **JWT**)
- Deux rôles : **utilisateur** et **administrateur**
- Liste des salles (capacité, équipements) et créneaux déjà réservés
- Réservation d'une salle avec **détection des conflits** (impossible de réserver un créneau déjà pris, ni dans le passé)
- Consultation et annulation de ses réservations (l'admin voit et peut annuler toutes les réservations)
- L'admin ajoute et supprime des salles

## Technologies

| Partie | Outils |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA / Hibernate, Bean Validation |
| Frontend | Angular 19 (composants standalone, signals, guard, intercepteur HTTP) |
| Base de données | PostgreSQL (H2 en mémoire pour les tests) |
| Tests | JUnit 5, Mockito, MockMvc |
| DevOps | Docker, Docker Compose, Nginx, GitHub Actions |

## Lancer le projet (avec Docker)

Il faut seulement Docker.

```bash
docker compose up --build
```

Puis ouvrir **http://localhost:8081**.

Compte administrateur créé au premier démarrage :

- email : `admin@reservationsalles.local`
- mot de passe : `admin123`

(modifiable avec les variables `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `JWT_SECRET`, `DB_PASSWORD`, voir `docker-compose.yml`)

## Lancer en développement (sans Docker pour le code)

1. Base PostgreSQL : `docker compose up db -d` (ajouter `ports: ["5432:5432"]` au service `db` si besoin)
2. Backend : `cd backend` puis `mvn spring-boot:run` (API sur http://localhost:8080)
3. Frontend : `cd frontend`, `npm install`, `npm start` (site sur http://localhost:4200, les appels `/api` sont envoyés au backend grâce à `proxy.conf.json`)

## Tests

```bash
cd backend
mvn test
```

- `ReservationServiceTest` : règles de réservation (dates, conflit, annulation) avec Mockito
- `ReservationApiTest` : API complète avec base H2 (401 sans token, 403 pour un simple utilisateur, 409 en cas de conflit)

## API REST

| Méthode | Route | Accès |
|---|---|---|
| POST | `/api/auth/inscription` | public |
| POST | `/api/auth/connexion` | public |
| GET | `/api/salles` | connecté |
| GET | `/api/salles/{id}/creneaux` | connecté |
| POST / PUT / DELETE | `/api/salles` | admin |
| GET | `/api/reservations` | connecté (ses réservations, ou toutes pour l'admin) |
| POST | `/api/reservations` | connecté |
| DELETE | `/api/reservations/{id}` | propriétaire ou admin |

## Structure

```
reservation-salles/
├── backend/            API Spring Boot
│   └── src/main/java/com/khadidja/reservationsalles/
│       ├── controller/   routes REST
│       ├── service/      logique métier (règles de réservation)
│       ├── repository/   accès base de données (JPA)
│       ├── model/        entités (Utilisateur, Salle, Reservation)
│       ├── security/     JWT et configuration Spring Security
│       └── exception/    gestion des erreurs
├── frontend/           application Angular
│   └── src/app/
│       ├── pages/        connexion, salles, réservations
│       ├── services/     appels API et authentification
│       └── core/         modèles, guard, intercepteur JWT
├── docker-compose.yml  base + backend + frontend
└── .github/workflows/  intégration continue (tests + build + images Docker)
```

## Pistes d'amélioration

- Migrations de base de données avec Flyway au lieu de `ddl-auto=update`
- Vue calendrier pour les disponibilités
- Notifications par email
- Tests Angular
