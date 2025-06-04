package com.bw.petclinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfiguration {

    /**
     * We need to set the USER ROLES in Authentication.authorities, the default Authentication.principal.authorities
     * will have OIDC_USER, SCOPE_openid and SCOPE_profile in it, which are not what we want.
     *
     * @return
     */
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            for (GrantedAuthority authority : authorities) {
                // Check for OidcUserAuthority because Spring Security 5.2 returns
                // each scope as a GrantedAuthority, which we don't care about.
                if (!(authority instanceof OidcUserAuthority)) {
                    continue;
                }
                OidcIdToken idToken = ((OidcUserAuthority) authority).getIdToken();
                if (idToken == null) {
                    continue;
                }
                List<String> roles = idToken.getClaimAsStringList("roles");
                if (roles == null) {
                    continue;
                }
                return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
            }
            return new HashSet<>();
        };
    }

//    @Bean
//    public UserDetailsManager userDetailsManager(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET,
                                "/owners/new/**",
                                "/owners/edit/**",
                                "/pets/new/**",
                                "/pets/edit/**",
                                "/visits/new/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/owners/**",
                                "/pets/**",
                                "/visits/**",
                                "/vets/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/owners/**",
                                "/pets/**",
                                "/visits/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
//                .formLogin(login -> login.loginPage("/login").loginProcessingUrl("/authenticate").permitAll())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout.permitAll().clearAuthentication(true).invalidateHttpSession(true))
                .exceptionHandling(handler -> handler.accessDeniedPage("/access-denied"));
        return http.build();
    }

}
