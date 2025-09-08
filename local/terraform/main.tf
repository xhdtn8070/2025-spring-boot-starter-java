# AWS Provider 설정 (LocalStack용)
provider "aws" {
  region                      = "ap-northeast-2"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true

  # S3_use_path_style은 S3 서비스 제거로 불필요
}

# SNS Topic 생성
resource "aws_sns_topic" "chat_message" {
  name = "local-poc-chat-message-sns"
}

# SQS Queue 생성: local-poc-chat-sqs-websocket
resource "aws_sqs_queue" "websocket_queue" {
  name = "local-poc-chat-sqs-websocket"
}

# SQS Queue 생성: local-poc-chat-sqs-push
resource "aws_sqs_queue" "push_queue" {
  name = "local-poc-chat-sqs-push"
}

# SQS Queue 생성: local-poc-chat-sqs-confirm
resource "aws_sqs_queue" "confirm_queue" {
  name = "local-poc-chat-sqs-confirm"
}

# --- SQS 큐 정책 정의 ---
# SQS Policy: local-poc-chat-sqs-websocket
resource "aws_sqs_queue_policy" "websocket_queue_policy" {
  queue_url = aws_sqs_queue.websocket_queue.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid = "Allow-SNS-SendMessage",
        Effect = "Allow",
        Principal = {
          AWS = "*"
        },
        Action = "sqs:SendMessage",
        Resource = aws_sqs_queue.websocket_queue.arn,
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.chat_message.arn
          }
        }
      }
    ]
  })
}

# SQS Policy: local-poc-chat-sqs-push
resource "aws_sqs_queue_policy" "push_queue_policy" {
  queue_url = aws_sqs_queue.push_queue.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid = "Allow-SNS-SendMessage",
        Effect = "Allow",
        Principal = {
          AWS = "*"
        },
        Action = "sqs:SendMessage",
        Resource = aws_sqs_queue.push_queue.arn,
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.chat_message.arn
          }
        }
      }
    ]
  })
}

# SQS Policy: local-poc-chat-sqs-confirm
resource "aws_sqs_queue_policy" "confirm_queue_policy" {
  queue_url = aws_sqs_queue.confirm_queue.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid = "Allow-SNS-SendMessage",
        Effect = "Allow",
        Principal = {
          AWS = "*"
        },
        Action = "sqs:SendMessage",
        Resource = aws_sqs_queue.confirm_queue.arn,
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.chat_message.arn
          }
        }
      }
    ]
  })
}

# SNS Topic과 SQS Queue 구독 연결
resource "aws_sns_topic_subscription" "websocket_subscription" {
  topic_arn = aws_sns_topic.chat_message.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.websocket_queue.arn
}

resource "aws_sns_topic_subscription" "push_subscription" {
  topic_arn = aws_sns_topic.chat_message.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.push_queue.arn
}

resource "aws_sns_topic_subscription" "confirm_subscription" {
  topic_arn = aws_sns_topic.chat_message.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.confirm_queue.arn
}