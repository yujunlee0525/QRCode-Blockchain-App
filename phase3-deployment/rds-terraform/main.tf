# provider "aws" {
#   region = "us-east-1"
# }
# // get vpc created by eksctl
# data "aws_vpc" "vpc" {
#   tags = var.tags
# }
# // get subnets
# data "aws_subnets" "subnet" {
#   tags = var.tags
# }
# resource "aws_security_group" "rds_sg" {
#   name        = "${var.cluster_name}-rds-sg"
#   description = "Security group for Aurora RDS"
#   vpc_id      = data.aws_vpc.vpc.id
#   ingress {
#     from_port   = 3306
#     to_port     = 3306
#     protocol    = "tcp"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   egress {
#     from_port   = 0
#     to_port     = 0
#     protocol    = "-1"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   tags = var.tags
# }
#
# resource "aws_rds_cluster" "aurora_cluster" {
#   cluster_identifier      = var.cluster_name
#   engine                  = "aurora-mysql"
#   engine_version          = var.rds_engine_version
#   database_name           = var.database_name
#   master_username         = var.master_username
#   master_password         = var.master_password
#   vpc_security_group_ids  = [aws_security_group.rds_sg.id]
#   db_subnet_group_name    = aws_db_subnet_group.rds_subnet_group.name
#   backup_retention_period = 7
#   preferred_backup_window = "07:00-09:00"
#   tags = var.tags
# }
#
# resource "aws_db_subnet_group" "rds_subnet_group" {
#   name       = "${var.cluster_name}-subnet-group"
#   subnet_ids = data.aws_subnets.subnet.ids
#
#   tags = var.tags
# }
#
# resource "aws_rds_cluster_instance" "aurora_instances" {
#   count              = 2 # Create 2 instances for high availability
#   identifier         = "${var.cluster_name}-instance-${count.index + 1}"
#   cluster_identifier = aws_rds_cluster.aurora_cluster.id
#   instance_class     = var.rds_instance_class
#   engine             = "aurora-mysql"
#
#   tags = var.tags
# }
provider "aws" {
  region = "us-east-1"
}

// Create a new security group for RDS
resource "aws_security_group" "rds_sg" {
  name        = "${var.cluster_name}-rds-sg"
  description = "Security group for Aurora RDS"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags
}

// Use default VPC and subnets
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default_subnets" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

// Subnet group for Aurora RDS
resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "${var.cluster_name}-subnet-group"
  subnet_ids = data.aws_subnets.default_subnets.ids

  tags = var.tags
}

// Aurora RDS cluster
resource "aws_rds_cluster" "aurora_cluster" {
  cluster_identifier      = var.cluster_name
  engine                  = "aurora-mysql"
  engine_version          = var.rds_engine_version
  database_name           = var.database_name
  master_username         = var.master_username
  master_password         = var.master_password
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  db_subnet_group_name    = aws_db_subnet_group.rds_subnet_group.name
  backup_retention_period = 7
  preferred_backup_window = "07:00-09:00"

  tags = var.tags
}

// Aurora RDS instances
resource "aws_rds_cluster_instance" "aurora_instances" {
  count              = 2 # Create 2 instances for high availability
  identifier         = "${var.cluster_name}-instance-${count.index + 1}"
  cluster_identifier = aws_rds_cluster.aurora_cluster.id
  instance_class     = var.rds_instance_class
  engine             = "aurora-mysql"

  tags = var.tags
}