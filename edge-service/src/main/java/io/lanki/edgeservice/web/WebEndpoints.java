package io.lanki.edgeservice.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class WebEndpoints {

  @Bean
  public RouterFunction<ServerResponse> routerFunction() {
    return RouterFunctions.route()
        .GET(
            "/note-service-fallback",
            request ->
                ServerResponse.ok()
                    .body(
                        Mono.just("TODO: Implement a fallback logic for note service"),
                        String.class))
        .POST(
            "/note-service-fallback",
            request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build())
        .build();
  }
}
