package io.lanki.edgeservice.config;

import static org.mockito.Mockito.when;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import(SecurityConfig.class)
public class SecurityConfigTests {

  @Autowired WebTestClient webTestClient;

  @MockBean ReactiveClientRegistrationRepository clientRegistrationRepository;

  @Test
  @DisplayName("Test logout not authenticated with no CSRF token which returns 403")
  void testLogoutNotAuthenticatedNoCsrfToken() {
    webTestClient.post().uri("/logout").exchange().expectStatus().isForbidden();
  }

  @Test
  @DisplayName("Test logout authenticated with no CSRF token which returns 403")
  public void testLogoutAuthenticatedNoCsrfToken() {
    webTestClient
        .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
        .post()
        .uri("/logout")
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test logout authenticated with CSRF token which returns 302")
  void testLogoutAuthenticatedCsrfToken() {
    when(clientRegistrationRepository.findByRegistrationId("test"))
        .thenReturn(Mono.just(testClientRegistration()));

    webTestClient
        .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
        .mutateWith(SecurityMockServerConfigurers.csrf())
        .post()
        .uri("/logout")
        .exchange()
        .expectStatus()
        .isFound();
  }

  private ClientRegistration testClientRegistration() {
    return ClientRegistration.withRegistrationId("test")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .clientId("test")
        .authorizationUri("https://sso.lanki.com/auth")
        .tokenUri("https://sso.lanki.com/token")
        .redirectUri("https://lanki.com")
        .build();
  }
}
