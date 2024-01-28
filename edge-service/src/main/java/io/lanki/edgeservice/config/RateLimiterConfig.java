package io.lanki.edgeservice.config;

import java.security.Principal;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

  @Bean
  public KeyResolver keyResolver() {
    // Principal is the currently authenticated user from the current request (e.g., exchange).
    // Extract the username from the principal but if the request is unauthenticated, it uses
    // "anonymous" as the default key for rate limiting.
    return exchange -> exchange.getPrincipal().map(Principal::getName).defaultIfEmpty("anonymous");
  }
}
