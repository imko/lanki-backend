package io.lanki.edgeservice.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.lanki.edgeservice.config.SecurityConfig;
import io.lanki.edgeservice.user.User;
import io.lanki.edgeservice.user.UserController;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTests {

  @Autowired private WebTestClient webTestClient;

  @MockBean ReactiveClientRegistrationRepository clientRegistrationRepository;

  @Test
  @DisplayName("Test unauthenticated user redirect")
  public void testNotAuthenticated() {
    webTestClient.get().uri("/user").exchange().expectStatus().is3xxRedirection();
  }

  @Test
  @DisplayName("Test authenticated user and return user context")
  public void testAuthenticated() {
    var expectedUser = new User("username", "first_name", "last_name", List.of("basic"));

    webTestClient
        .mutateWith(configureMockOidcLogin(expectedUser))
        .get()
        .uri("/user")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(User.class)
        .value(user -> assertThat(user).isEqualTo(expectedUser));
  }

  private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User expectedUser) {
    return SecurityMockServerConfigurers.mockOidcLogin()
        .idToken(
            builder -> {
              builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username());
              builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName());
              builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName());
            });
  }
}
