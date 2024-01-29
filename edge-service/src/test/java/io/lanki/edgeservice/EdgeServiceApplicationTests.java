package io.lanki.edgeservice;

import com.redis.testcontainers.RedisContainer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class EdgeServiceApplicationTests {

  private static final int REDIS_PORT = 6379;

  @Container
  private static RedisContainer container =
      new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.redis.host", () -> container.getHost());
    registry.add("spring.redis.port", () -> container.getMappedPort(REDIS_PORT));
  }

  @Test
  @DisplayName("Test Redis connection")
  void testRedisConnection() {
    // Retrieve the Redis URI from the container
    String redisURI = container.getRedisURI();
    RedisClient client = RedisClient.create(redisURI);
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      RedisCommands<String, String> commands = connection.sync();
      Assertions.assertEquals("PONG", commands.ping());
    }
  }
}
