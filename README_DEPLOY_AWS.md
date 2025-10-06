Déploiement AWS (ECR + ECS Fargate)

Ce dépôt contient des workflows et templates pour automatiser la construction et le déploiement sur Amazon ECR + ECS (Fargate). Les fichiers ajoutés sont :

- `.github/workflows/deploy-to-aws-ecs.yml` : workflow GitHub Actions qui build, push et déploie 3 images (feedservice, tamagoservice, tamago-front).
- `ecs-task-definitions/*.json` : templates de task definition ECS (à ajuster selon vos ressources : secrets, variables, volumes).
- `tamago-front/Dockerfile` et `.dockerignore` : Dockerfile pour builder et lancer le front en production.

Étapes manuelles / secrets à configurer

1) Créer 3 repos ECR (ou un par service) :
   - feedservice
   - tamagoservice
   - tamago-front

2) Créer un cluster ECS et des services Fargate correspondants (ou utiliser des services existants). Notez les noms de cluster et des services.

3) Ajouter les secrets GitHub repository secrets utilisés par le workflow :
   - AWS_ACCESS_KEY_ID : clé IAM avec droits ECR/ECS
   - AWS_SECRET_ACCESS_KEY
   - ECR_REGISTRY : ex "123456789012.dkr.ecr.us-east-1.amazonaws.com"
   - ECR_FEED_REPO : nom du repo ECR pour feedservice
   - ECR_TAMAGO_REPO : nom du repo ECR pour tamagoservice
   - ECR_FRONT_REPO : nom du repo ECR pour tamago-front
   - ECS_CLUSTER : nom du cluster ECS
   - ECS_FEED_SERVICE : nom du service ECS pour feedservice
   - ECS_TAMAGO_SERVICE : nom du service ECS pour tamagoservice
   - ECS_FRONT_SERVICE : nom du service ECS pour tamago-front

4) Adapter les fichiers `ecs-task-definitions/*.json` :
   - remplacer REPLACE_WITH_IMAGE par l'image ECR (le workflow remplace automatiquement si vous utilisez les actions de rendu, sinon vous pouvez entrer manuellement)
   - configurer les variables d'environnement et secrets (ex : DB_HOST, APP_JWT_SECRET) pour pointer vers RDS ou autre service de base de données et garder les secrets dans AWS or Secrets Manager

5) (Optionnel) Créer un RDS MySQL et configurer un endpoint accessible depuis les tasks ECS (ou utiliser Amazon Aurora/MySQL). Mettez à jour `SPRING_DATASOURCE_URL` dans les task definitions ou configurez via secrets/SSM.

6) Lancer le workflow en poussant sur `main`/`master`. Le workflow buildera, poussera les images et déploiera aux services ECS.

Notes et limitations

- Le workflow suppose que vos Dockerfiles dans `feedservice` et `tamagoservice` sont valides (ils existent dans le repo). Si vos services sont des jars Spring Boot (comme le dossier `feedservice/target` le suggère), vérifiez que leurs Dockerfiles construisent correctement l'image.
- Pour production, évitez de stocker APP_JWT_SECRET en clair dans le repo; utilisez AWS Secrets Manager et passez la valeur via `secrets` dans la task definition.
- Vous pouvez étendre le workflow pour mettre à jour un load balancer, gérer DNS (Route53) ou créer automatiquement le cluster et les services via CloudFormation/Terraform.

Besoin d'aide pour :

- créer les repos ECR / IAM policy et les secrets GitHub automatiquement (je peux fournir une CloudFormation ou Terraform)
- adapter les task definitions pour utiliser Secrets Manager / Param Store
- ajouter un job pour exécuter des DB migrations (Flyway/liquibase)

Dites-moi ce que vous voulez automatiser ensuite (CloudFormation/Terraform pour tout provisionner, usage de Secrets Manager, routage via ALB/Route53...).
