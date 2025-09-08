package org.tikim.sample.redisson.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonValueTest extends SpringIntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("Key-Value 저장/조회가 정상적으로 동작한다")
    void setAndGet() {
        String key = "test:value:basic";
        String value = "Hello Redis!";

        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value);

        String result = bucket.get();
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("TTL이 지난 데이터는 조회되지 않는다")
    void ttlExpiry() throws InterruptedException {
        String key = "test:value:ttl";
        String value = "Temporary";

        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value, Duration.ofSeconds(1));

        // 1.5초 대기하여 TTL 만료 유도
        Thread.sleep(1500);

        String result = bucket.get();
        assertThat(result).isNull();
    }
}
