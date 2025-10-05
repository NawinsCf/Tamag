# Tamago — Projet

Ce dépôt contient l'ensemble du projet Tamago : front-end (Next.js), et services backend (Java/Maven) `feedservice/` et `tamagoservice/`.
Il s'agit d'un petit projet fait dans le cadre de mes études dans le but d'apprendre et faire l'integration de A à Z d'un projet utilisant deux services.

Résumé
------
Le front a été développé initialement comme prototype (front temporaire) pour valider les parcours utilisateurs et tester l'intégration avec les services backend. Les fonctions minimales principales sont implémentées.

# Tamago — Projet

Ce dépôt contient le projet Tamago :
- Frontend : `tamago-front/` (Next.js, App Router, TypeScript)
- Backends : `feedservice/` et `tamagoservice/` (Spring Boot, Java, Maven)
- Base de données : MySQL (déclarée dans `docker-compose.yml`)

But du README
-------------
Fournir des instructions claires pour démarrer le projet en local (Docker Compose), lancer le front en développement, et résoudre les problèmes courants rencontrés en dev.

Architecture et ports
---------------------
- `db` (MySQL) : 3306
- `feedservice` (backend tamago/feeding) : exposé sur le host 8081 -> container 8080
- `tamagoservice` (backend principal / auth / tamagotype) : exposé sur le host 8082 -> container 8080
- `tamago-front` (Next.js dev) : 3000 (local, pas dockerisé par défaut)

Prérequis
---------
- Docker & Docker Compose
- Java 17 + Maven (si tu veux lancer les services Java localement sans Docker)
- Node.js (version compatible, npm) pour `tamago-front`
- Git (pour pousser/puller)

Démarrage rapide (Docker Compose)
--------------------------------
1) Build + lancer tous les services (DB + backends) :

```bash
docker compose up --build
```

2) Vérifier les logs :

```bash
docker compose logs -f feedservice
docker compose logs -f tamagoservice
```

3) Ouvrir le front (en local, dossier `tamago-front`) :

```bash
cd tamago-front
npm install
npm run dev
# Next écoute sur http://localhost:3000
```

Notes importantes pour le développement
--------------------------------------
- En dev le front utilise des rewrites pour proxyfier `/api/*` vers les backends afin de préserver les cookies HttpOnly entre le navigateur et les services. Si tu modifies `next.config.ts` (rewrites), redémarre le serveur Next.
- Si tu changes `next.config.ts` ou installe des dépendances, stoppe et relance `npm run dev`.
- Lors des développements sur les backends, si tu utilises Docker, rebuild/recreate le service :

```bash
docker compose build feedservice
docker compose up -d --no-deps --force-recreate feedservice
```

Variables d'environnement utiles
--------------------------------
- Pour `tamago-front` (fichier `.env.local`) :
  - `NEXT_PUBLIC_FEEDSERVICE_BASE=http://localhost:8081` (dev)
  - `NEXT_PUBLIC_TAMAGOSERVICE_BASE=http://localhost:8082` (dev)

- Pour `feedservice` et `tamagoservice` (variables passées au container ou via Docker Compose) :
  - `APP_JWT_SECRET` : secret partagé pour signer/verifier les JWT d'accès (dev : utiliser la même valeur pour les services qui vérifient le token)
  - `SPRING_DATASOURCE_*` : si tu wishes override DB connection

Fonctionnement rapide de l'auth (pour debug)
-----------------------------------------
- Auth locale utilise cookies HttpOnly : `tmg_at` (JWT court) et `tmg_rt` (refresh token opaque, persistant côté serveur).
- Important : les services qui valident le cookie JWT doivent partager `APP_JWT_SECRET` en dev. Sinon FeedService retournera 401 sur appels proxys depuis le front.

Endpoints utiles
----------------
- GET  /api/tamago               -> liste des tamagos (feedservice)
- POST /api/tamago/{id}/calculefaim -> recalcul état à t (feedservice)
- POST /api/tamago/{id}/nourrir -> nourrir un tamago (feedservice)
- POST /api/user/choose-tamago  -> choix/creation d'un tamago (feedservice, infère l'utilisateur depuis le JWT)
- POST /api/auth/login, /api/auth/refresh, /api/auth/logout -> endpoints d'auth (tamagoservice)

Debug & problèmes courants
--------------------------
- Next dev écoute sur 3000 mais les requêtes `/api/*` peuvent timeout si Next est bloqué (redémarrer Next et consulter la console du serveur Next aide).
- Si `feedservice` renvoie 401 lors d'appels depuis le front : vérifier que `APP_JWT_SECRET` est identique entre `tamagoservice` (qui signe) et `feedservice` (qui vérifie).
- Si Flyway n'a pas migré : vérifier les logs des services au démarrage ; Flyway s'exécute au démarrage du service qui contient les migrations.
- Pour reconstruire un service Java localement (sans Docker) :

```bash
cd feedservice
./mvnw clean package
./mvnw spring-boot:run
```

Commandes utiles (Git Bash)
--------------------------
Voici un petit flot sûr à exécuter depuis Git Bash. Remplace le message et la branche si besoin.

1) Vérifier la branche courante :

```bash
git status
git rev-parse --abbrev-ref HEAD
```

2) Synchroniser & rebaser tes changements locaux sur `origin` :

```bash
git fetch origin
git pull --rebase origin $(git rev-parse --abbrev-ref HEAD)
```

3) Ajouter et valider :

```bash
git add -A
git commit -m "Ton message clair ici"
```

4) Pousser sur la branche courante :

```bash
git push origin $(git rev-parse --abbrev-ref HEAD)
# ou (premier push) :
git push -u origin $(git rev-parse --abbrev-ref HEAD)
```

5) Si tu dois forcer (à éviter), utilise la forme plus sûre :

```bash
git push --force-with-lease origin $(git rev-parse --abbrev-ref HEAD)
```

6) Créer une nouvelle branche et la pousser :

```bash
git checkout -b feature/ma-fonction
# changements, commit
git push -u origin feature/ma-fonction
```

Tests rapides & scripts d'intégration
------------------------------------
- Des scripts Node d'intégration existent dans `scripts/` (ex.: `choose-cross-host.js`) pour tester login → refresh → appel cross-service. Ils peuvent être lancés localement (ils utilisent fetch + tough-cookie pour simuler un navigateur).

Maintenance des données existantes
---------------------------------
- La correction appliquée pour l'initialisation d'un Tamago (création via `/api/user/choose-tamago`) met désormais `pv`/`pf` à partir du `Tamagotype` par défaut. Les Tamagos déjà en base (anciennes créations) ne sont pas modifiées automatiquement ; si tu veux, je peux fournir un script SQL pour mettre à jour les anciens Tamagos.

Besoin d'aide ?
--------------
Si tu veux que j'ajoute :
- un script SQL de migration pour corriger les Tamagos existants,
- un petit script bash pour automatiser add→commit→pull-rebase→push,
- ou que je mette à jour le README avec des captures d'écran / commandes Windows PowerShell spécifiques,

dis‑le et je l'ajoute.
