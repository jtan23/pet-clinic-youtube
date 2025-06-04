package com.bw.owner.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Value("${spring.security.oauth2.resource-server.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * We need JWT Decoder from Oauth2 Authorization Server to decode the JWT token in the request.
     *
     * @return
     */
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter());
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, "/owners/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/owners/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/owners/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

}
