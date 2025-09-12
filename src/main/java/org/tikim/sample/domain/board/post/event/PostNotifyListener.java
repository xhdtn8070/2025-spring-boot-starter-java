package org.tikim.sample.domain.board.post.event;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.SqsHeaders; // ← 요기!
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Map;

@Slf4j
@Component
public class PostNotifyListener {

    @SqsListener(value = "local-board-post-notify-sqs")
    public void onMessage(
            String body,
            @Header(SqsHeaders.SQS_SOURCE_DATA_HEADER) Message sqsMessage, // 원본 SQS 메시지
            @Headers Map<String, Object> headers
    ) {
        log.info("[POST_NOTIFY] id={}, body={}", sqsMessage.messageId(), body);

        // SNS Message Attributes를 쓰려면 아래처럼 원본에서 읽을 수 있어요
        var attrs = sqsMessage.messageAttributes();
        var event = attrs.get("event");
        var audience = attrs.get("audience");
        log.debug("[POST_NOTIFY] attrs event={}, audience={}", event, audience);

        // TODO: 알림 처리
    }
}
