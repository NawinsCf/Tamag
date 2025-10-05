resource "aws_ecr_repository" "feed" {
  name = "${var.project_name}-feedservice"
}

resource "aws_ecr_repository" "tamago" {
  name = "${var.project_name}-tamagoservice"
}

resource "aws_ecr_repository" "front" {
  name = "${var.project_name}-front"
}

resource "aws_ecs_cluster" "this" {
  name = "${var.project_name}-cluster"
}

# IAM role for ECS tasks
resource "aws_iam_role" "ecs_task_execution" {
  name = "${var.project_name}-ecs-task-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_assume_role.json
}

data "aws_iam_policy_document" "ecs_task_assume_role" {
  statement {
    effect = "Allow"
    principals {
      type = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_policy" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Security group for ECS tasks
resource "aws_security_group" "ecs_tasks_sg" {
  name        = "${var.project_name}-ecs-tasks-sg"
  description = "Security group for ECS tasks"
  vpc_id      = var.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Security group for RDS
resource "aws_security_group" "rds_sg" {
  name        = "${var.project_name}-rds-sg"
  description = "Allow DB access"
  vpc_id      = var.vpc_id

  # Allow ECS tasks security group to connect to RDS on MySQL port
  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks_sg.id]
  }

  # Keep an optional admin CIDR rule if provided (for DB administration)
  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = [var.allowed_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Subnet group for RDS
resource "aws_db_subnet_group" "default" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = var.private_subnet_ids
}

resource "aws_db_instance" "default" {
  identifier              = "${var.project_name}-db"
  allocated_storage       = 20
  engine                  = "mysql"
  engine_version          = "8.0"
  instance_class          = "db.t3.micro"
  name                    = "tamag"
  username                = var.db_username
  password                = var.db_password
  skip_final_snapshot     = true
  publicly_accessible     = false
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  db_subnet_group_name    = aws_db_subnet_group.default.name
}

output "ecr_registry" {
  value = aws_ecr_repository.feed.repository_url
}

output "ecr_repos" {
  value = {
    feed   = aws_ecr_repository.feed.repository_url
    tamago = aws_ecr_repository.tamago.repository_url
    front  = aws_ecr_repository.front.repository_url
  }
}

output "ecs_cluster_arn" {
  value = aws_ecs_cluster.this.arn
}
