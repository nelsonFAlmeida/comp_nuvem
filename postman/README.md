Microservices Postman collection
================================

Import the collection `Microservices.postman_collection.json` into Postman to test the API Gateway and microservices deployed neste projeto.

IP e URL do projeto (valores usados nesta coleção):

- EC2 public IP: 108.131.80.229
- Base URL do API Gateway: http://108.131.80.229:8080

Setup:

1. Importe o ficheiro JSON para o Postman (File > Import).
2. Abra as variáveis da coleção e confirme que `ec2_ip` está definido para `108.131.80.229`.
3. `base_url` resolve para `http://{{ec2_ip}}:8080`.

Principais endpoints (via API Gateway):

- Health: `GET http://108.131.80.229:8080/actuator/health`
- Swagger UI: `http://108.131.80.229:8080/swagger-ui.html` (abrir no browser)
- Users: `GET http://108.131.80.229:8080/api/users` | `POST http://108.131.80.229:8080/api/users`
- Products: `GET http://108.131.80.229:8080/api/products` | `POST http://108.131.80.229:8080/api/products`
- Orders: `GET http://108.131.80.229:8080/api/orders` | `POST http://108.131.80.229:8080/api/orders`

Notas importantes:

- Antes de testar, assegure que a máquina EC2 está ativa e que o deployment (via Ansible/docker-compose) foi executado com sucesso.
- Se o IP mudar (após novo apply do Terraform), atualize a variável `ec2_ip` na coleção do Postman.

