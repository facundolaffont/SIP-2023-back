package com.example.helloworld.config.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
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
    WebSecurityCustomizer webSecurity() {

        // Selecciona todas las URL que no comiencen con '/api/v1/'.
        // Las URL que matcheen con esta expresión regular no serán
        // procesadas por el backend.
        final var exclusionRegex = "^(?!%s).*$".formatted(
            "/api/v1/");

        return web -> web.ignoring()
            .regexMatchers(exclusionRegex);

    }

    @Bean
    SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {

        return http // Tipo HttpSecurity

            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests

                // Permite el acceso a todas las rutas sólo si el usuario está identificado.
                .antMatchers(
                    "/api/v1/students/students-registration-check",
                    "/api/v1/students/register-students"
                ) // Devuelve AuthorizedUrl.
                    .hasAuthority("Docente") // Devuelve AuthorizationManagerRequestMatcherRegistry.

                // Permite el acceso a todas las rutas sólo si el usuario está identificado.
                .anyRequest() // Devuelve AuthorizedUrl.
                    .authenticated() // Devuelve AuthorizationManagerRequestMatcherRegistry.

            ) // Devuelve HttpSecurity.

            .cors(withDefaults()) // Devuelve HttpSecurity.

            .oauth2ResourceServer(server -> server

                .authenticationEntryPoint(authenticationErrorHandler) // Devuelve
                                                                        // OAuth2ResourceServerConfigurer.
                .jwt() // Devuelve JwtConfigurer.
                .decoder(makeJwtDecoder()) // Devuelve JwtConfigurer.
                .jwtAuthenticationConverter(makePermissionsConverter()) // Devuelve JwtConfigurer.

            ) // Devuelve HttpSecurity.

            // Deshabilita el chequeo de token CSRF.
            .csrf(csrf -> csrf
                .disable()) // Devuelve HttpSecurity.

            .build(); // Devuelve DefaultSecurityFilterChain.

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(dotenv.get("CLIENT_ORIGIN_URL")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type", "Authorization"));

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
            "https://datatracker.ietf.org/doc/html/rfc6750#section-3.1");

        return token.getAudience().contains(applicationProps.getAudience())
            ? OAuth2TokenValidatorResult.success()
            : OAuth2TokenValidatorResult.failure(audienceError);

    }

    private JwtAuthenticationConverter makePermissionsConverter() {

        final var jwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        //jwtAuthoritiesConverter.setAuthoritiesClaimName("permissions"); // Código original
        jwtAuthoritiesConverter.setAuthoritiesClaimName("https://hello-world.example.com/roles");

        jwtAuthoritiesConverter.setAuthorityPrefix("");

        final var jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(jwtAuthoritiesConverter);

        return jwtAuthConverter;

    }

    // private class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken>  {
        
    //     @Override
    //     public AbstractAuthenticationToken convert(Jwt jwt) {
    //         Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

    //         // Create an authentication token
    //         return new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, authorities);
    //     }

    //     private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    //         Map<String, Object> claims = jwt.getClaims();
    //         List<String> roles = (List<String>) claims.get("https://hello-world.example.com/roles");

    //         Collection<GrantedAuthority> authorities = new ArrayList<>();
    //         if (roles != null) {
    //             for (String role : roles) {
    //                 authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    //             }
    //         }
    //         return authorities;
    //     }
    // }

}