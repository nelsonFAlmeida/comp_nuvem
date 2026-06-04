terraform {
  required_version = ">= 1.0.0"

  backend "s3" {
    bucket         = "finalproject-tf-state-eu-west-1"
    key            = "envs/dev/terraform.tfstate"
    region         = "eu-west-1"
    dynamodb_table = "finalproject-tf-locks"
    encrypt        = true
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "3.2.1"
    }
  }
}

# Default tags applied to every AWS resource
provider "aws" {
  region = "eu-west-1"

  default_tags {
    tags = {
      Project     = "comp_nuvem"
      Environment = "main"
      ManagedBy   = "terraform"
    }
  }
}
