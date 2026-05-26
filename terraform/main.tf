provider "aws" {
  region = "eu-west-1"
}

resource "null_resource" "exemplo" {
  provisioner "local-exec" {
    command = "echo 'Estrutura Terraform pronta yeet'"
  }
}
