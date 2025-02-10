variable "cluster_name" {
  type        = string
  default     = "twitter-phase-3-aurora"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "rds_instance_class" {
  type        = string
  default     = "db.r6g.large"
}

variable "rds_engine_version" {
  type        = string
  default     = "8.0.mysql_aurora.3.05.2"
}

variable "database_name" {
  type        = string
  default     = "twitter_db"
}

variable "master_username" {
  type        = string
  default     = "admin"
}

variable "master_password" {
  type        = string
  default     = "theCloudCrew580" # Change to a secure password or use a secret manager.
}

variable "tags" {
  type        = map(string)
  default     = {
    Project = "twitter-phase-3"
  }
}