# Tamago — Projet

Ce dépôt contient l'ensemble du projet Tamago : front-end (Next.js), et services backend (Java/Maven) `feedservice/` et `tamagoservice/`.

Résumé
------
Le front a été développé initialement comme prototype (front temporaire) pour valider les parcours utilisateurs et tester l'intégration avec les services backend. Les fonctions minimales principales sont implémentées.

Fonctions minimales implémentées (exemples)
- Authentification basique (inscription/connexion)
- Pages principales pour l'utilisateur (enregistrement, démarrage, choix de tamago)
- Création de tamagos
- Upload & suppression d'images via endpoints Next.js

Utilisation des API
-------------------
Le front s'attend à une URL de base pour les APIs backend configurée via une variable d'environnement publique :

  NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

Le backend est constitué de services Java dans les dossiers `feedservice/` et `tamagoservice/`.

Démarrage local
----------------
Prérequis : Java (Maven) pour les services backend, Node.js pour le front.

1) Lancer les services backend (exemples) :

```powershell
# feedservice
cd feedservice
./mvnw spring-boot:run

# tamagoservice
cd ..\tamagoservice
./mvnw spring-boot:run
```

2) Lancer le front (Next.js) :

```powershell
cd tamago-front
npm install
npm run dev
```

Notes et détails pratiques
-------------------------

- Ports exposés par `docker-compose.yml` (par défaut) :
  - MySQL (db) : 3306
  - feedservice : 8081 (container 8080)
  - tamagoservice : 8082 (container 8080)

- Variables d'environnement recommandées pour le `tamago-front` (mettre dans `.env.local` pour le dev) :
  - `NEXT_PUBLIC_FEEDSERVICE_BASE=http://localhost:8081`
  - `NEXT_PUBLIC_TAMAGOSERVICE_BASE=http://localhost:8082`
  (le front utilisera ces URL pour appeler les endpoints des deux services)

- Migrations & ordre de démarrage
  - Les migrations Flyway sont fournies dans `tamagoservice/src/main/resources/db/migration`.
  - Pour rendre le démarrage plus robuste (éviter les courses entre MySQL et les services), des scripts `wait-for-db.sh` ont été ajoutés aux images des deux services (`feedservice/wait-for-db.sh` et `tamagoservice/wait-for-db.sh`).
    Ces scripts attendent que MySQL accepte les connexions avant de lancer l'application (et donc Flyway).

- Commandes utiles (PowerShell)

  # Monter tout en local (rebuild)
  docker-compose up --build

  # Monter en arrière-plan
  docker-compose up -d --build

  # Voir les logs (exemples)
  docker-compose logs -f db
  docker-compose logs -f feedservice
  docker-compose logs -f tamagoservice

- Vérifier Flyway (depuis le conteneur MySQL) :
  docker-compose exec db mysql -uroot -proot -e "USE tamag; SHOW TABLES; SELECT * FROM flyway_schema_history\G"

- Points d'amélioration suggérés :
  - Ajouter `healthcheck` et `restart` dans `docker-compose.yml` pour redonner de la robustesse au déploiement local.
  - Remplacer/centraliser les migrations dans un service ou job dédié en environnement CI/CD si le projet grossit.


Git — commandes prêtes à exécuter (PowerShell)
---------------------------------------------
Remplace `<REMOTE_URL>` par l'URL de ton dépôt distant (GitHub/GitLab/Bitbucket).

Initialiser et pousser le dépôt (si git non initialisé) :

```powershell
cd C:\BAC4\Tamago_VF\Tamago
# configure l'utilisateur si nécessaire
git config --global user.name "Ton Nom" ;
git config --global user.email "ton.email@example.com" ;
# initialise et pousse
git init ;
git add . ;
git commit -m "Initial commit — Tamago project" ;
git branch -M main ;
git remote add origin <REMOTE_URL> ;
git push -u origin main
```

Mettre à jour et pousser des changements :

```powershell
cd C:\BAC4\Tamago_VF\Tamago
git add -A ;
git commit -m "Mise à jour: front + logos + README" ;
git push
```

---

Si tu veux, je peux exécuter ces commandes ici (si `git` est installé dans l'environnement). Sinon, tu peux les lancer depuis PowerShell localement.