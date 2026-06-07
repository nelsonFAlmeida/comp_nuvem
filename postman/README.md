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

Como executar os testes automáticos (Postman):

1. Abra o Postman e selecione a coleção "Microservices API".
2. Clique em "Runner" (Collection Runner), escolha a coleção e execute. Verifique os testes na aba "Test Results".

Executar via `newman` (linha de comando):

```bash
# instalar newman (se não tiver)
npm install -g newman

# executar a collection com a variável ec2_ip
newman run postman/Microservices.postman_collection.json --env-var "ec2_ip=108.131.80.229"
```

Passos rápidos de troubleshooting se receber `500 Internal Server Error`:

1) Conectar por SSH ao EC2 (substitua a chave se necessário):

```bash
ssh -i ~/.ssh/finalproject-ec2-key.pem ubuntu@108.131.80.229
```

2) Verificar containers em execução:

```bash
sudo docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
sudo docker compose -f /opt/myproject/docker-compose.yml ps
```

3) Ver logs dos serviços (ajuste nomes se necessário):

```bash
sudo docker compose -f /opt/myproject/docker-compose.yml logs api-gateway --tail 200
sudo docker compose -f /opt/myproject/docker-compose.yml logs user-service --tail 200
```

4) Ver ficheiro `.env` usado pelo Compose (checar credenciais/URLs):

```bash
sudo cat /opt/myproject/.env
```

5) Testar conectividade com a base de dados RDS:

```bash
sudo apt-get update && sudo apt-get install -y netcat
nc -vz finalproject-db.czw48k28woy1.eu-west-1.rds.amazonaws.com 5432
```

Cole aqui as saídas de `docker ps`, dos logs (`api-gateway` e `user-service`) e do `cat /opt/myproject/.env` para eu analisar e sugerir correções precisas.


