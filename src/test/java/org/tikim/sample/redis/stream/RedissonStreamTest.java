package org.tikim.sample.redis.stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonStreamTest extends SpringIntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    private final String streamKey = "chat:stream";
    private final String groupName = "chat-group";
    private final String consumerName = "worker-1";

    @BeforeEach
    void setUp() {
        RStream<String, String> stream = redissonClient.getStream(streamKey);
        boolean exists = false;
        try {
            exists = stream.listGroups().stream().anyMatch(g -> groupName.equals(g.getName()));
        } catch (Exception ignored) { /* stream 미존재 시 예외 가능 → 아래에서 makeStream 처리 */ }

        if (!exists) {
            stream.createGroup(StreamCreateGroupArgs.name(groupName).makeStream());
        }
    }

    @AfterEach
    void tearDown() {
        redissonClient.getStream(streamKey).delete();
    }

    @Test
    @DisplayName("컨슈머 그룹이 있는 Redisson Stream에서 메시지 추가 후 그룹으로 읽기")
    void addAndReadWithGroup() {
        RStream<String, String> stream = redissonClient.getStream(streamKey);

        // Given: 메시지 추가
        StreamAddArgs<String, String> args = StreamAddArgs.entries(Map.of(
            "type", "join",
            "user", "test-user"
        ));
        StreamMessageId messageId = stream.add(args);

// readGroup 결과는 Map<StreamMessageId, Map<String, String>>
        Map<StreamMessageId, Map<String, String>> records = stream.readGroup(
            groupName,
            consumerName,
            StreamReadGroupArgs.neverDelivered().count(1).timeout(Duration.ofSeconds(3))
        );

        assertThat(records).hasSize(1);

// 하나만 읽었으니 첫 엔트리 꺼내서 검증
        Map.Entry<StreamMessageId, Map<String, String>> entry = records.entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo(messageId);
        assertThat(entry.getValue()).containsExactlyInAnyOrderEntriesOf(Map.of(
            "type", "join",
            "user", "test-user"
        ));

// ack
        long acked = stream.ack(groupName, entry.getKey());
        assertThat(acked).isEqualTo(1L);

    }
}
