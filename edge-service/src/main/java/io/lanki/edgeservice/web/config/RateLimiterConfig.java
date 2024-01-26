package io.lanki.edgeservice.web.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

  @Bean
  public KeyResolver keyResolver() {
    // TODO: There is no authenticated user, so we will temporarily use custom KeyResolver.
    return exchange -> Mono.just("anonymous");
  }

}
