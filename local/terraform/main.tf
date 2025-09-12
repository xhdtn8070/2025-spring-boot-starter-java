# AWS Provider (LocalStack용)
provider "aws" {
  region                      = "ap-northeast-2"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true
}

# ─────────────────────────────────────────────────────────────
# SNS: reply 이벤트 토픽 (1개)
resource "aws_sns_topic" "board_reply" {
  name = "local-board-reply-sns"
}

# SQS: post 소유자 알림 큐
resource "aws_sqs_queue" "post_notify" {
  name = "local-board-post-notify-sqs"
}

# SQS: reply 작성자 액션 큐
resource "aws_sqs_queue" "reply_action" {
  name = "local-board-reply-action-sqs"
}

# ─────────────────────────────────────────────────────────────
# SQS 정책: 해당 토픽에서의 SendMessage 허용 (각 큐에 부여)

resource "aws_sqs_queue_policy" "post_notify_policy" {
  queue_url = aws_sqs_queue.post_notify.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Sid       = "Allow-SNS-SendMessage"
      Effect    = "Allow"
      Principal = { AWS = "*" }
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.post_notify.arn
      Condition = { ArnEquals = { "aws:SourceArn" = aws_sns_topic.board_reply.arn } }
    }]
  })
}

resource "aws_sqs_queue_policy" "reply_action_policy" {
  queue_url = aws_sqs_queue.reply_action.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Sid       = "Allow-SNS-SendMessage"
      Effect    = "Allow"
      Principal = { AWS = "*" }
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.reply_action.arn
      Condition = { ArnEquals = { "aws:SourceArn" = aws_sns_topic.board_reply.arn } }
    }]
  })
}

# ─────────────────────────────────────────────────────────────
# SNS → SQS 구독 (2개) : 필터 정책 불필요하면 이대로

resource "aws_sns_topic_subscription" "post_notify_sub" {
  topic_arn = aws_sns_topic.board_reply.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.post_notify.arn
}

resource "aws_sns_topic_subscription" "reply_action_sub" {
  topic_arn = aws_sns_topic.board_reply.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.reply_action.arn
}

# (선택) 구독 필터 정책을 쓰고 싶다면 아래처럼 attribute JSON 추가
# resource "aws_sns_topic_subscription" "post_notify_sub" {
#   topic_arn             = aws_sns_topic.board_reply.arn
#   protocol              = "sqs"
#   endpoint              = aws_sqs_queue.post_notify.arn
#   filter_policy         = jsonencode({ event = ["REPLY_CREATED"], audience = ["POST_OWNER"] })
# }
# resource "aws_sns_topic_subscription" "reply_action_sub" {
#   topic_arn             = aws_sns_topic.board_reply.arn
#   protocol              = "sqs"
#   endpoint              = aws_sqs_queue.reply_action.arn
#   filter_policy         = jsonencode({ event = ["REPLY_CREATED"], audience = ["REPLY_AUTHOR"] })
# }
