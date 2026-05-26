resource "null_resource" "exemplo" {
  provisioner "local-exec" {
    command = "echo 'Terraform ok'"
  }
}
