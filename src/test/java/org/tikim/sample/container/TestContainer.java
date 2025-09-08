package org.tikim.sample.container;

import org.springframework.test.context.DynamicPropertyRegistry;

public interface TestContainer {
    void start();
    void register(DynamicPropertyRegistry registry);
}
