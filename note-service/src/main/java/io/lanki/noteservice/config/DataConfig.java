package io.lanki.noteservice.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing
public class DataConfig {

  @Bean
  public AuditorAware<String> auditorAware() {
    /* Extract the username of currently authenticated user from SecurityContextHolder. */
    return () ->
        Optional
            // Extract SecurityContext from SecurityContextHolder.
            .ofNullable(SecurityContextHolder.getContext())
            // Extract Authentication from SecurityContext.
            .map(SecurityContext::getAuthentication)
            // Handle the case where user is not authenticated.
            .filter(Authentication::isAuthenticated)
            // Extract username from Authentication.
            .map(Authentication::getName);
  }
}
