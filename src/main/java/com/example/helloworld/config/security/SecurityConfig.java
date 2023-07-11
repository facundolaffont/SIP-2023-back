package com.example.helloworld.config.security;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.helloworld.config.ApplicationProperties;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Bean
  public WebSecurityCustomizer webSecurity() {

    // Selecciona todas las URL que no comiencen con '/api/v1/'.
    // Las URL que matcheen con esta expresión regular no serán
    // procesadas por el backend.
    final var exclusionRegex = "^(?!%s).*$".formatted(
      "/api/v1/"
    );

    return web ->
      web.ignoring()
        .regexMatchers(exclusionRegex);
  }

  @Bean
  public SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {

    // return http.authorizeRequests()
    //   // .antMatchers("/api/v1/users/add") // DEBUG
    //   .antMatchers("**")
    //     .authenticated()
    //   .anyRequest()
    //       .permitAll()
    //   .and()
    //     .cors()
    //   .and()
    //     .oauth2ResourceServer()
    //       .authenticationEntryPoint(authenticationErrorHandler)
    //       .jwt()
    //         .decoder(makeJwtDecoder())
    //         .jwtAuthenticationConverter(makePermissionsConverter())
    //       .and()
    //   .and()
    //     .build();

    // return http.authorizeHttpRequests()
    //   .antMatchers("**")
    //     .authenticated()
    //   .anyRequest()
    //       .permitAll()
    //   .and()
    //     .cors()
    //   .and()
    //     .oauth2ResourceServer()
    //       .authenticationEntryPoint(authenticationErrorHandler)
    //       .jwt()
    //         .decoder(makeJwtDecoder())
    //         .jwtAuthenticationConverter(makePermissionsConverter())
    //       .and()
    //   .and()
    //     .build();

    return
        http // Tipo HttpSecurity

        //   .authorizeHttpRequests() // Devuelve AuthorizationManagerRequestMatcherRegistry.
        //     .antMatchers("**") // Devuelve AuthorizedUrl.
        //   .authenticated() // Devuelve AuthorizationManagerRequestMatcherRegistry.
        //     .anyRequest() // Devuelve AuthorizedUrl.
        //   .permitAll() // Devuelve AuthorizationManagerRequestMatcherRegistry.
        // .and() // Devuelve HttpSecurity.

        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
          .antMatchers("**") // Devuelve AuthorizedUrl.
            .authenticated() // Devuelve AuthorizationManagerRequestMatcherRegistry.
          .anyRequest() // Devuelve AuthorizedUrl.
            .permitAll() // Devuelve AuthorizationManagerRequestMatcherRegistry.
        ) // Devuelve HttpSecurity.
        
        .cors(withDefaults()) // Devuelve HttpSecurity.
        .oauth2ResourceServer(server -> server
          .authenticationEntryPoint(authenticationErrorHandler) // Devuelve OAuth2ResourceServerConfigurer.
            .jwt() // Devuelve JwtConfigurer.
            .decoder(makeJwtDecoder()) // Devuelve JwtConfigurer.
            .jwtAuthenticationConverter(makePermissionsConverter()) // Devuelve JwtConfigurer.
        )  // Devuelve HttpSecurity.

        // .csrf().disable() // DEBUG

        .csrf(csrf -> csrf
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        ) // Devuelve HttpSecurity.
      .build(); // Devuelve DefaultSecurityFilterChain.

  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList(dotenv.get("CLIENT_ORIGIN_URL")));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList(
      "Content-Type"
      , "Authorization"
      // , "X-XSRF-TOKEN"
    ));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;

  }


  /* Private */

  private final AuthenticationErrorHandler authenticationErrorHandler;
  private final OAuth2ResourceServerProperties resourceServerProps;
  private final ApplicationProperties applicationProps;
  private final Dotenv dotenv = Dotenv.configure()
    .ignoreIfMissing()
    .load();
  
  private JwtDecoder makeJwtDecoder() {
    
    final var issuer = resourceServerProps.getJwt().getIssuerUri();
    final var decoder = JwtDecoders.<NimbusJwtDecoder>fromIssuerLocation(issuer);
    final var withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
    final var tokenValidator = new DelegatingOAuth2TokenValidator<>(withIssuer, this::withAudience);

    decoder.setJwtValidator(tokenValidator);
    return decoder;

  }

  private OAuth2TokenValidatorResult withAudience(final Jwt token) {

    final var audienceError = new OAuth2Error(
      OAuth2ErrorCodes.INVALID_TOKEN,
      "El token no fue emitido para la audiencia especificada",
      "https://datatracker.ietf.org/doc/html/rfc6750#section-3.1"
    );

    return token.getAudience().contains(applicationProps.getAudience())
      ? OAuth2TokenValidatorResult.success()
      : OAuth2TokenValidatorResult.failure(audienceError);

  }

  private JwtAuthenticationConverter makePermissionsConverter() {

    final var jwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtAuthoritiesConverter.setAuthoritiesClaimName("permissions");
    jwtAuthoritiesConverter.setAuthorityPrefix("");

    final var jwtAuthConverter = new JwtAuthenticationConverter();
    jwtAuthConverter.setJwtGrantedAuthoritiesConverter(jwtAuthoritiesConverter);

    return jwtAuthConverter;

  }

}