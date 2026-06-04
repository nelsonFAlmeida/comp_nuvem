output "ec2_public_ip" {
  description = "Public IP of the EC2 instance for Ansible"
  value       = aws_instance.app_server.public_ip
}

output "rds_endpoint" {
  description = "Endpoint of the RDS PostgreSQL instance"
  value       = module.db.db_instance_endpoint
}

output "sqs_queue_url" {
  description = "URL of the SQS Queue"
  value       = aws_sqs_queue.order_events.url
}
