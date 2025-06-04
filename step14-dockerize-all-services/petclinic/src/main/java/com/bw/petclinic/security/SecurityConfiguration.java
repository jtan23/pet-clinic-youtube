package com.bw.petclinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configRequests(http);
        configFormLogin(http);
        configLogout(http);
        configExceptionHandling(http);
        return http.build();
    }

    private void configRequests(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
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
                .anyRequest().permitAll());
    }

    private void configFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .permitAll());
    }

    private void configLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .permitAll()
                .clearAuthentication(true)
                .invalidateHttpSession(true));
    }

    private void configExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(handler -> handler.accessDeniedPage("/access-denied"));
    }

}
