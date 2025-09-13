package org.tikim.sample.domain.board.reply.event;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.SqsHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import org.tikim.sample.domain.board.reply.service.application.ReplyApplicationService;
import org.tikim.sample.domain.board.reply.service.application.dto.ReplyDetailServiceResponse;
import org.tikim.sample.domain.outbox.dto.ReplyCreatedOutboxPayload;
import org.tikim.sample.global.event.sqs.dto.SnsParsedMessage;
import org.tikim.sample.global.event.sqs.util.SnsBodyParser;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyActionListener {

    private final SnsBodyParser parser;
    private final ReplyApplicationService replyApplicationService;

    @SqsListener("local-board-reply-action-sqs")
    public void onMessage(String body,
        @Header(SqsHeaders.SQS_SOURCE_DATA_HEADER) Message sqsMessage) {
        try {
            SnsParsedMessage<ReplyCreatedOutboxPayload> m = parser.parse(body, ReplyCreatedOutboxPayload.class);
            var p = m.payload();

            log.info("[REPLY_ACTION] sqsId={}, snsId={}, postId={}, replyId={}, postOwnerId={}, replyAuthorId={}, attrs={}",
                sqsMessage.messageId(), m.snsMessageId(),
                p.postId(), p.replyId(), p.postOwnerId(), p.replyAuthorId(),m.messageAttributes());


            ReplyDetailServiceResponse reply = replyApplicationService.get(p.replyId());
            log.info("[REPLY_ACTION] reply detail: {}", reply);

        } catch (Exception e) {
            log.error("[REPLY_ACTION] handle failed. id={}, body={}", sqsMessage.messageId(), body, e);
            throw new IllegalStateException("ReplyActionListener failed", e);
        }
    }
}
