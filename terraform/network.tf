data "aws_availability_zones" "available" {}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = "finalproject-vpc"
  cidr = "10.0.0.0/16"

  azs              = slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnets   = ["10.0.101.0/24", "10.0.102.0/24"]
  database_subnets = ["10.0.201.0/24", "10.0.202.0/24"]

  create_database_subnet_group       = true
  create_database_subnet_route_table = true
  enable_dns_hostnames               = true

  # Do not create NAT Gateways to save costs
  enable_nat_gateway = false
  single_nat_gateway = false
}
