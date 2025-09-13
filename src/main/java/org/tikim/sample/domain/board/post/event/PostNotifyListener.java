package org.tikim.sample.domain.board.post.event;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.SqsHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import org.tikim.sample.domain.board.post.service.application.PostApplicationService;
import org.tikim.sample.domain.board.post.service.application.dto.PostDetailServiceResponse;
import org.tikim.sample.domain.outbox.dto.ReplyCreatedOutboxPayload;
import org.tikim.sample.global.event.sqs.dto.SnsParsedMessage;
import org.tikim.sample.global.event.sqs.util.SnsBodyParser;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostNotifyListener {

    private final SnsBodyParser parser;
    private final PostApplicationService postApplicationService;

    @SqsListener("local-board-post-notify-sqs")
    public void onMessage(String body,
        @Header(SqsHeaders.SQS_SOURCE_DATA_HEADER) Message sqsMessage) {
        try {
            SnsParsedMessage<ReplyCreatedOutboxPayload> m = parser.parse(body, ReplyCreatedOutboxPayload.class);
            ReplyCreatedOutboxPayload p = m.payload();

            PostDetailServiceResponse post = postApplicationService.get(p.postId());
            Long postOwnerId = post.authorId();


            log.info("[POST_NOTIFY] sqsId={}, snsId={}, postId={}, replyId={}, postOwnerId={}, replyAuthorId={}, attrs={}",
                sqsMessage.messageId(), m.snsMessageId(),
                p.postId(), p.replyId(), p.postOwnerId(), p.replyAuthorId(), m.messageAttributes());
            // 예: 알림 처리
            // notifyService.notifyPostOwner(postOwnerId, p.postId(), p.replyId(), p.authorId(), m.messageAttributes());

        } catch (Exception e) {
            log.error("[POST_NOTIFY] handle failed. id={}, body={}", sqsMessage.messageId(), body, e);
            throw new IllegalStateException("PostNotifyListener failed", e);
        }
    }
}
