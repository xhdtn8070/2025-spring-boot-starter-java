package org.tikim.sample.redisson.pubsub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonPubSubTest extends SpringIntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("Redisson Pub/Sub: 발행한 메시지를 구독자가 수신한다")
    void publishAndReceive() throws Exception {
        String topicName = "test:pubsub";
        RTopic topic = redissonClient.getTopic(topicName, StringCodec.INSTANCE);

        List<String> received = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        // 리스너 등록
        int listenerId = topic.addListener(String.class, (channel, msg) -> {
            received.add(msg);
            latch.countDown();
        });

        try {
            // 발행
            topic.publish("Hello Redis PubSub!");

            // 수신 대기 및 검증
            boolean completed = latch.await(3, TimeUnit.SECONDS);
            assertThat(completed).isTrue();
            assertThat(received).hasSize(1);
            assertThat(received.get(0)).isEqualTo("Hello Redis PubSub!");
        } finally {
            // 리스너 해제 (청소)
            topic.removeListener(listenerId);
        }
    }
}
