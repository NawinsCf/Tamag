# Tamago — Projet

Ce dépôt contient l'ensemble du projet Tamago : front-end (Next.js), et services backend (Java/Maven) `feedservice/` et `tamagoservice/`.

Résumé
------
Le front a été développé initialement comme prototype (front temporaire) pour valider les parcours utilisateurs et tester l'intégration avec les services backend. Les fonctions minimales principales sont implémentées.

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
-----------------------------------------
cd feedservice
git checkout -b feature/ma-fonction
- Des scripts Node d'intégration existent dans `scripts/` (ex.: `choose-cross-host.js`) pour tester login → refresh → appel cross-service. Ils peuvent être lancés localement (ils utilisent fetch + tough-cookie pour simuler un navigateur).
--------------
- `DEPLOY_USER` — SSH user (e.g. `ec2-user`)
````markdown
# Tamago — Projet

Ce dépôt contient le projet Tamago :
- Frontend : `tamago-front/` (Next.js, App Router, TypeScript)
- Backends : `feedservice/` et `tamagoservice/` (Spring Boot, Java, Maven)
- Base de données : MySQL (déclarée dans `docker-compose.yml`)

Résumé
------
Le front a été développé initialement comme prototype pour valider les parcours utilisateurs et tester l'intégration avec les services backend. Les fonctions principales sont implémentées.

But du README
-------------
Fournir des instructions claires pour démarrer le projet en local (Docker Compose), lancer le front en développement, et résoudre les problèmes courants rencontrés en dev.

Architecture et ports
---------------------
- `db` (MySQL) : 3306
- `feedservice` (backend tamago/feeding) : host 8081 -> container 8080
- `tamagoservice` (backend principal / auth / tamagotype) : host 8082 -> container 8080
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
- Si tu changes `next.config.ts` ou installes des dépendances, stoppe et relance `npm run dev`.
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
  - `APP_JWT_SECRET` : secret partagé pour signer/vérifier les JWT d'accès (dev : utiliser la même valeur pour les services qui vérifient le token)
  - `SPRING_DATASOURCE_*` : si tu souhaites override la connexion DB

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
APP_JWT_SECRET=replace_with_secure_jwt_secret
```

2) Synchroniser & rebaser tes changements locaux sur `origin` :

```bash
git fetch origin
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/tamag
```

3) Ajouter et valider :

```bash
git add -A
SPRING_DATASOURCE_USERNAME=root
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
SPRING_DATASOURCE_PASSWORD=replace_with_db_password
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

## Deployment (CI/CD)

This project uses GitHub Actions to build Docker images, push them to a registry, and deploy to an AWS server using SSH + docker-compose. Release management with `release-please` is recommended (releases trigger the deploy workflow).

### Workflows
- `.github/workflows/build-and-push.yml` — builds service images (feedservice, tamagoservice, tamago-front) using `docker buildx` and pushes them to a registry (GHCR or Docker Hub). This runs on pushes to `main`.
- `.github/workflows/deploy-on-release.yml` — triggered on GitHub release `published`. It copies `docker-compose.prod.yml` to the remote server, writes the production `.env` from the `PROD_DOTENV` secret, then uses SSH to run `docker login` (if Docker credentials are set) and `docker compose pull && docker compose up -d` on the server.

### Required GitHub Secrets
Add the following repository secrets (Repository Settings → Secrets → Actions):

- `DEPLOY_HOST` — public IP or hostname of the AWS host
- `DEPLOY_USER` — SSH user (e.g. `ec2-user`)
- `DEPLOY_SSH_KEY` — private SSH key (PEM) for the `DEPLOY_USER` (add the public key to the `~/.ssh/authorized_keys` on the host)
- `DEPLOY_SSH_PORT` — optional (default: 22)
- `PROD_DOTENV` — full contents of the production `.env` file (the deploy workflow will write this to `~/deploy/.env` on the server)
- `DOCKER_USERNAME` — Docker registry username (used for `docker login` on the server)
- `DOCKER_PASSWORD` — Docker registry password or personal access token (recommended)

Optionally:
- `GHCR_PAT` — Personal access token if you push to GHCR and need a token for authentication from the workflow

### How the deploy workflow uses the secrets
- `PROD_DOTENV` is written to `~/deploy/.env` on the server; `docker-compose.prod.yml` references these variables.
- If `DOCKER_USERNAME`/`DOCKER_PASSWORD` are set, the workflow performs `docker login` on the remote host (piping the password via stdin) before pulling private images. After deploy it performs `docker logout`.

### Example `PROD_DOTENV` contents
(Keep this secret. Use real secure values in production.)

```env
MYSQL_ROOT_PASSWORD=replace_with_strong_password
MYSQL_DATABASE=tamag
APP_JWT_SECRET=replace_with_secure_jwt_secret
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/tamag
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=replace_with_db_password
NEXT_PUBLIC_FEEDSERVICE_BASE=http://your-server:8081
NEXT_PUBLIC_TAMAGOSERVICE_BASE=http://your-server:8082
TAMAGOSERVICE_STORAGE_IMG_DIR=IMG
APP_COOKIE_ACCESS_NAME=tmg_at
APP_COOKIE_REFRESH_NAME=tmg_rt
APP_COOKIE_SECURE=true
APP_COOKIE_SAME_SITE=Strict
APP_COOKIE_DOMAIN=yourdomain.com
APP_COOKIE_PATH=/
```

### Server prerequisites
On the AWS host (`DEPLOY_HOST`):
- Docker and Docker Compose must be installed and available to the `DEPLOY_USER`.
- The `DEPLOY_USER` must have permission to run `docker` (either via `sudo` or being in the `docker` group).
- Ensure necessary ports are open in the instance security group (80/443 and any backend ports you want to expose).

### Triggering a deploy
1. Create a release on GitHub (manually or via `release-please`).
2. The `deploy-on-release` workflow runs: it copies `docker-compose.prod.yml` and writes `PROD_DOTENV` to the server, logs in to Docker (if creds provided), pulls images and restarts containers.

### Troubleshooting tips
- If the workflow fails with SSH errors: verify `DEPLOY_HOST`, `DEPLOY_USER`, and `DEPLOY_SSH_KEY` are correct and that the key is allowed on the server.
- If `docker compose pull` fails with unauthorized errors: check `DOCKER_USERNAME`/`DOCKER_PASSWORD` (use a registry token, not an account password).
- Verify server logs via SSH:

```bash
ssh -i path/to/key.pem $DEPLOY_USER@$DEPLOY_HOST
docker compose -f ~/deploy/docker-compose.prod.yml logs -f
docker ps
```

### Security notes
- Do not commit `.env` with production secrets. Put production variables in `PROD_DOTENV` secret in GitHub.
- Use registry tokens instead of account passwords.
- Rotate tokens/keys regularly.

---

Updated deployment instructions added. If you want, I can also:
- Add a small script to the repo to create a `PROD_DOTENV` from a local `.env` file and print instructions to copy it into GitHub Secrets safely.
- Add automatic cleanup of `~/.docker/config.json` on the remote host after logout.
