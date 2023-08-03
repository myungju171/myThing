package com.project.mything.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class TestContainer {
    private final static int REDIS_PORT = 6379;
    static final String REDIS_IMAGE = "redis:6-alpine";
    static final String MYSQL_IMAGE = "mysql:8";
    static final MySQLContainer MYSQL_CONTAINER;
    static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);

        MYSQL_CONTAINER = (MySQLContainer) new MySQLContainer(MYSQL_IMAGE)
                .withDatabaseName("test")
                .withPassword("1234")
                .withUsername("root")
                .withReuse(true);

        REDIS_CONTAINER.start();
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> "" + REDIS_CONTAINER.getMappedPort(REDIS_PORT));
        registry.add("spring.redis.password", () -> "password");
    }
}