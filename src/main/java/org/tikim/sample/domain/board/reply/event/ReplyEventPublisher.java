// ReplyEventPublisher.java
package org.tikim.sample.domain.board.reply.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tikim.sample.domain.outbox.dto.ReplyCreatedOutboxPayload;
import org.tikim.sample.global.event.sns.properties.SnsProperties;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
@RequiredArgsConstructor
public class ReplyEventPublisher {

    private static final String TOPIC_KEY = "board-reply";

    private final SnsClient snsClient;
    private final SnsProperties snsProps;
    private final ObjectMapper objectMapper;

    public void publishReplyCreated(ReplyCreatedOutboxPayload payload) {
        String topicArn = snsProps.getTopics().get(TOPIC_KEY);
        String message = toJson(payload);

        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)      // 속성 없이 그대로 발행
                .build());
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize payload", e);
        }
    }
}
