# Terraform: provision minimal AWS infra for Tamago

Ce dossier contient une configuration Terraform minimale pour provisionner :

- 3 repos ECR (feedservice, tamagoservice, tamago-front)
- ECS cluster
- IAM role pour l'exécution de tâches ECS
- RDS MySQL (instance simple)

Important : cette configuration est intentionnellement minimale pour te donner un point de départ. Tu dois fournir le VPC et les subnets (déjà existants) via `terraform.tfvars` ou variables d'environnement.

Pré-requis :
- Terraform 1.x
- AWS CLI configured localement (ou variables d'environnement AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY)

Exemple d'utilisation :

1) Copier le fichier d'exemple et adapter :

   cp terraform/terraform.tfvars.example terraform/terraform.tfvars

2) Initialiser et appliquer :

   terraform -chdir=terraform init
   terraform -chdir=terraform plan
   terraform -chdir=terraform apply

Notes :
- Les mots de passe et valeurs sensibles doivent être fournis via `terraform.tfvars` ou via des variables d'environnement.
- En production, préfère RDS Multi-AZ, snapshots, des classes d'instance plus grandes et l'utilisation de Secrets Manager pour stocker les identifiants.
