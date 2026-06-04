resource "aws_sqs_queue" "order_events" {
  name = "order-events-queue"
}
