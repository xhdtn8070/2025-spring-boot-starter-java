package org.tikim.sample.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public final class ValKeyTestContainer implements TestContainer {

    public static final ValKeyTestContainer INSTANCE = new ValKeyTestContainer();

    private final GenericContainer<?> container = new GenericContainer<>(
            DockerImageName.parse("valkey/valkey:8.0")
    )
            .withExposedPorts(6379)
            .withReuse(true);

    private ValKeyTestContainer() { }

    @Override
    public void start() {
        if (!container.isRunning()) {
            container.start();
        }
    }

    @Override
    public void register(DynamicPropertyRegistry registry) {
        registry.add("redisson.host", container::getHost);
        registry.add("redisson.port", () -> container.getMappedPort(6379));
        registry.add("redisson.password", () -> "");
        registry.add("redisson.timeout", () -> 3000);
        registry.add("redisson.use-ssl", () -> false);
    }
}
