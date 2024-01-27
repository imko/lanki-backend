package io.lanki.edgeservice.config;

import java.nio.charset.Charset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(
      ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
    CookieServerCsrfTokenRepository tokenRepository =
        CookieServerCsrfTokenRepository.withHttpOnlyFalse();
    XorServerCsrfTokenRequestAttributeHandler delegate =
        new XorServerCsrfTokenRequestAttributeHandler();
    // Use only the handle() method of XorServerCsrfTokenRequestAttributeHandler and the
    // default implementation of resolveCsrfTokenValue() from ServerCsrfTokenRequestHandler
    ServerCsrfTokenRequestHandler requestHandler = delegate::handle;

    return http
        // Use a cookie-based strategy for exchanging CSRF tokens.
        .csrf(c -> c.csrfTokenRepository(tokenRepository).csrfTokenRequestHandler(requestHandler))
        .authorizeExchange(
            exchange ->
                exchange
                    .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico")
                    .permitAll()
                    .pathMatchers(HttpMethod.GET, "/v1/api/notes/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        // Respond with Unauthorized for exceptions thrown when user is unauthenticated.
        .exceptionHandling(
            exceptionHandling -> {
              exceptionHandling.accessDeniedHandler(
                  (exchange, ex) ->
                      exchange
                          .getPrincipal()
                          .flatMap(
                              principal -> {
                                var response = exchange.getResponse();
                                response.setStatusCode(
                                    principal instanceof AnonymousAuthenticationToken
                                        ? HttpStatus.UNAUTHORIZED
                                        : HttpStatus.FORBIDDEN);
                                response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
                                var dataBufferFactory = response.bufferFactory();
                                var buffer =
                                    dataBufferFactory.wrap(
                                        ex.getMessage().getBytes(Charset.defaultCharset()));
                                return response
                                    .writeWith(Mono.just(buffer))
                                    .doOnError(error -> DataBufferUtils.release(buffer));
                              }));
            })
        .oauth2Login(Customizer.withDefaults())
        .logout(
            logout ->
                logout
                    // Remove SESSION cookie.
                    .logoutHandler(logoutHandler())
                    .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
        .build();
  }

  @Bean
  WebFilter csrfCookieWebFilter() {
    return (exchange, chain) -> {
      Mono<CsrfToken> csrfToken =
          exchange.getAttributeOrDefault(CsrfToken.class.getName(), Mono.empty());
      return csrfToken
          .doOnSuccess(
              token -> {
                /* Ensures the token is subscribed to. */
              })
          .then(chain.filter(exchange));
    };
  }

  private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
      ReactiveClientRegistrationRepository clientRegistrationRepository) {
    var oidcLogoutSuccessHandler =
        new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
    // Spring dynamically computes the base URL.
    oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
    return oidcLogoutSuccessHandler;
  }

  private DelegatingServerLogoutHandler logoutHandler() {
    return new DelegatingServerLogoutHandler(
        new SecurityContextServerLogoutHandler(), new WebSessionServerLogoutHandler());
  }
}
