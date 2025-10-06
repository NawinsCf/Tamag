variable "region" {
  type    = string
  default = "us-east-1"
}

variable "project_name" {
  type    = string
  default = "tamago"
}

variable "vpc_id" {
  description = "VPC id where ECS and RDS will be launched. Provide via tfvars or environment."
  type        = string
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs for ECS tasks and RDS (comma separated in tfvars)."
  type        = list(string)
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs for NAT/ALB if needed."
  type        = list(string)
}

variable "db_username" {
  type    = string
  default = "root"
}

variable "db_password" {
  description = "DB password - override in tfvars or via environment"
  type        = string
  sensitive   = true
}

variable "allowed_cidr" {
  description = "CIDR to allow access to database (for admin), e.g. your IP/32"
  type        = string
  default     = "0.0.0.0/0"
}

variable "github_owner" {
  description = "GitHub organization or user that owns the repository (used for OIDC trust)."
  type        = string
}

variable "github_repo" {
  description = "GitHub repository name (used for OIDC trust)."
  type        = string
}
