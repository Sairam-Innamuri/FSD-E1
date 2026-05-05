package com.campus.scems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/signin", "/login/**", "/register/**", "/about").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events/*").permitAll()
                        .requestMatchers("/faculty/**").hasRole("FACULTY")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .anyRequest().permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect(request.getContextPath() + "/login/student"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(request.getContextPath() + "/login/student?denied")))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/login/student?logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
