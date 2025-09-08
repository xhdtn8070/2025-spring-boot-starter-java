package org.tikim.sample.redis.lua;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonLuaScriptTest extends SpringIntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    private static final Logger log = LoggerFactory.getLogger(RedissonLuaScriptTest.class);

    private String loadScript(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("Lua script not found: " + path);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return br.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Lua: " + path, e);
        }
    }

    @Test
    @DisplayName("조건부 SET + 3초 TTL: 최초 OK, 재시도 LOCKED")
    void lockAndSet_basic() {
        final String scriptPath = "lua/lock-and-set.lua"; // src/test/resources/lua/lock-and-set.lua
        final String script = loadScript(scriptPath);
        final String key = "test:lua:lock-and-set";

        // 1) 키가 없을 때 실행 → OK 반환, 키 생성, TTL ≈ 3s
        String res1 = redissonClient.getScript(StringCodec.INSTANCE)
                .eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, List.of(key));
        assertThat(res1).isEqualTo("OK");

        RBucket<Object> bucket = redissonClient.getBucket(key);
        assertThat(bucket.isExists()).isTrue();

        long ttl1 = bucket.remainTimeToLive();
        assertThat(ttl1).isGreaterThan(0L);
        assertThat(ttl1).isLessThanOrEqualTo(TimeUnit.SECONDS.toMillis(3));

        // 2) TTL 만료 전 동일 스크립트 재실행 → LOCKED
        String res2 = redissonClient.getScript(StringCodec.INSTANCE)
                .eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, List.of(key));
        assertThat(res2).isEqualTo("LOCKED");
        assertThat(bucket.isExists()).isTrue();
    }

    @Test
    @DisplayName("TTL 만료 후 재실행 시 다시 OK 및 TTL 재설정")
    void lockAndSet_afterExpiry() throws InterruptedException {
        final String scriptPath = "lua/lock-and-set.lua";
        final String script = loadScript(scriptPath);
        final String key = "test:lua:lock-and-set-2";

        // 초기 실행 → OK
        String res = redissonClient.getScript(StringCodec.INSTANCE)
                .eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, List.of(key));
        assertThat(res).isEqualTo("OK");
        assertThat(redissonClient.getBucket(key).isExists()).isTrue();

        // 3.5초 대기(3초 TTL 만료 대기)
        Thread.sleep(3500);

        // 만료 후 재실행 → OK 및 TTL 재설정
        String resAfter = redissonClient.getScript(StringCodec.INSTANCE)
                .eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, List.of(key));
        assertThat(resAfter).isEqualTo("OK");

        var bucket = redissonClient.getBucket(key);
        assertThat(bucket.isExists()).isTrue();
        long ttl = bucket.remainTimeToLive();
        assertThat(ttl).isGreaterThan(0L);
        assertThat(ttl).isLessThanOrEqualTo(TimeUnit.SECONDS.toMillis(3));
    }
}
