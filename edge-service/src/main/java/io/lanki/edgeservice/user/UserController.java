package io.lanki.edgeservice.user;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

  // For Web MVC and WebFlux controllers, instead of using ReactiveSecurityContextHolder directly,
  // we can use @CurrentSecurityContext and @AuthenticationPrincipal to inject the SecurityContext
  // and the principal (e.g., OidcUser).
  @GetMapping
  public Mono<User> getUser(@AuthenticationPrincipal OidcUser oidcUser) {
    var user =
        new User(
            oidcUser.getPreferredUsername(),
            oidcUser.getGivenName(),
            oidcUser.getFamilyName(),
            oidcUser.getClaimAsStringList("roles"));
    return Mono.just(user);
  }
}
