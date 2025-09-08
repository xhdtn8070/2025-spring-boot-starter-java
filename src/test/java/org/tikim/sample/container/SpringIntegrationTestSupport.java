package org.tikim.sample.container;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public abstract class SpringIntegrationTestSupport {

    // 코틀린의 companion init 블록 대체: 클래스 로딩 시 컨테이너 기동
    static {
        ValKeyTestContainer.INSTANCE.start();
        MySQLTestContainer.INSTANCE.start();
        LocalStackTestContainer.INSTANCE.start();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        ValKeyTestContainer.INSTANCE.register(registry);
        MySQLTestContainer.INSTANCE.register(registry);
        LocalStackTestContainer.INSTANCE.register(registry);
    }
}
