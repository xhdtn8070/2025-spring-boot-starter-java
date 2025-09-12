package org.tikim.sample.domain.board.reply.event;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.SqsHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Map;

@Slf4j
@Component
public class ReplyActionListener {

    @SqsListener(value = "local-board-reply-action-sqs")
    public void onMessage(
            String body,
            @Header(SqsHeaders.SQS_SOURCE_DATA_HEADER) Message sqsMessage,
            @Headers Map<String, Object> headers
    ) {
        log.info("[REPLY_ACTION] id={}, body={}", sqsMessage.messageId(), body);

        var attrs = sqsMessage.messageAttributes();
        var event = attrs.get("event");
        var audience = attrs.get("audience");
        log.debug("[REPLY_ACTION] attrs event={}, audience={}", event, audience);

        // TODO: 후속 액션 처리
    }
}
