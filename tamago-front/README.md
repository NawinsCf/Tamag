FR: Tamago — Front-end (Next.js)

Ce répertoire contient le front-end Next.js du projet Tamago.

Résumé rapide
-------------
Un UI temporaire (prototype) a été mis en place initialement pour valider les flux et tester les intégrations avec les services backend. Aujourd'hui les fonctions minimales principales sont implémentées et utilisables :

- Auth (composant `src/context/AuthContext.tsx`) — inscription / connexion basiques.
- Pages: `register`, `start`, `choose-tamago`.
- Création de tamagos: `CreateTamagotypeModal`.
- Upload / suppression d'images: `pages/api/upload-image.js`, `pages/api/delete-image.js`.
- Composants réutilisables: `NavBar`, `LoginForm`, `Toast`, `Carousel`.

Utilisation des API
-------------------
Le front communique avec les services backend (ex. `feedservice/` et `tamagoservice/`) via une base URL configurée par variable d'environnement publique :

	NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

Les routes Next.js dans `pages/api/` servent de proxies/handlers (ex : upload d'images) et peuvent appeler `NEXT_PUBLIC_API_BASE_URL` pour relayer les requêtes.

Exemple d'utilisation côté client :

```js
// upload via endpoint Next.js
const form = new FormData();
form.append('file', file);
await fetch('/api/upload-image', { method: 'POST', body: form });

// appel direct au backend
const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/tamagos`);
```

État et recommandations
-----------------------
- Le front est volontairement simple pour accélérer le prototypage. Il faudra renforcer la validation, la gestion fine des erreurs et ajouter des tests avant production.
- Pour la mise en production, remplacer l'upload local par un stockage externe (S3, Cloud Storage) et configurer des variables secrètes côté serveur.

Fichiers utiles
--------------
- `public/cat-horn-logo.svg` — logo couleur (128×128)
- `public/cat-horn-bw.svg` — logo noir & blanc stylisé
- `pages/api/upload-image.js` — handler d'upload
- `pages/api/delete-image.js` — suppression d'image
- `src/context/AuthContext.tsx` — gestion auth

Démarrage local
---------------
Pré-requis : Node.js (v16+/18+ recommandé), npm ou pnpm

```powershell
cd tamago-front
npm install
npm run dev
```

Le front tourne sur `http://localhost:3000`.

Git — commandes recommandées (PowerShell)
---------------------------------------
Remplace `<REMOTE_URL>` par l'URL du dépôt (GitHub, GitLab...). Si git n'est pas installé, installe Git for Windows : https://git-scm.com/download/win

Initialiser et pousser (si dépôt non initialisé) :

```powershell
cd C:\BAC4\Tamago_VF\Tamago
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

Configuration utilisateur git (si nécessaire) :

```powershell
git config --global user.name "Ton Nom"
git config --global user.email "ton.email@example.com"
```

---

Ci-dessous se trouve le README par défaut généré par Next.js (conservé pour référence) :

This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.

