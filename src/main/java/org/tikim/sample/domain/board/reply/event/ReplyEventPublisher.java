// ReplyEventPublisher.java
package org.tikim.sample.domain.board.reply.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tikim.sample.global.event.sns.properties.SnsProperties;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReplyEventPublisher {

    private final SnsClient snsClient;
    private final SnsProperties snsProps;

    public void publishReplyCreated(Long replyId, Long postId, Long authorId) {
        String topicArn = snsProps.getTopics().get("board-reply");

        String payload = """
            {"replyId":%d,"postId":%d,"authorId":%d}
        """.formatted(replyId, postId, authorId);

        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message(payload)
                // (필요 시) 필터 정책 쓸 때 사용할 속성
                .messageAttributes(Map.of(
                    "event",    MessageAttributeValue.builder().dataType("String").stringValue("REPLY_CREATED").build(),
                    "audience", MessageAttributeValue.builder().dataType("String").stringValue("POST_OWNER").build()
                ))
                .build());
    }
}
