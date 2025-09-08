package org.tikim.sample.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class MySQLTestContainer implements TestContainer {

    public static final MySQLTestContainer INSTANCE = new MySQLTestContainer();

    private final MySQLContainer<?> container = new MySQLContainer<>(
            DockerImageName.parse("mysql:8.4.3")
    )
            .withDatabaseName("tikim")
            .withUsername("tikim")
            .withPassword("tikim-test")
            .withReuse(true);

    private MySQLTestContainer() {}

    @Override
    public void start() {
        if (!container.isRunning()) {
            container.start();
        }
    }

    @Override
    public void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }
}
