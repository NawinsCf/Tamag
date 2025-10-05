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
