// src/test/java/org/tikim/sample/domain/board/post/event/PostNotifyListenerIT.java
package org.tikim.sample.domain.board.post.event;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.tikim.sample.container.SpringIntegrationTestSupport;
import org.tikim.sample.domain.board.post.service.application.PostApplicationService;
import org.tikim.sample.domain.board.post.service.application.dto.PostDetailServiceResponse;
import org.tikim.sample.domain.outbox.dto.ReplyCreatedOutboxPayload;
import org.tikim.sample.global.event.sns.properties.SnsProperties;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

class PostNotifyListenerIT extends SpringIntegrationTestSupport {

    @Autowired private SnsClient snsClient;
    @Autowired private SnsProperties snsProps;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private PostApplicationService postApplicationService; // 리스너가 호출하는 seam


    private String topicArn;

    @BeforeEach
    void setUp() {
        topicArn = snsProps.getTopics().get("board-reply");
    }

    @Test
    void snsPublish_triggersListener_and_callsPostApplicationServiceGet() throws Exception {
        // given: 게시글/댓글 식별자와 작성자 정보 (테스트 데이터)
        Long postId = 101L;
        Long replyId = 1001L;
        Long postOwnerId = 2001L;
        Long replyAuthorId = 3001L;

        // 리스너가 호출할 서비스 응답 스텁 (NPE 방지용)
        when(postApplicationService.get(eq(postId)))
            .thenReturn(new PostDetailServiceResponse(
                postId, postOwnerId, "title", "content", null, null
            ));

        // SNS에 퍼블리시할 페이로드(JSON은 SNS가 SQS에 래핑해서 내려보냄)
        ReplyCreatedOutboxPayload payload = new ReplyCreatedOutboxPayload(
            replyId, postId, postOwnerId, replyAuthorId
        );
        String messageJson = objectMapper.writeValueAsString(payload);

        // when: SNS 퍼블리시 → SNS→SQS → 리스너 기동
        snsClient.publish(PublishRequest.builder()
            .topicArn(topicArn)
            .message(messageJson)
            .build());

        // then: 리스너가 PostApplicationService.get(postId) 호출했는지 검증
        verify(postApplicationService, timeout(5000)).get(eq(postId));
    }
}
