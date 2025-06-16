package fa.training.cms.security.config;

import fa.training.cms.security.core.AuthenticationFilter;
import fa.training.cms.security.core.TokenResolver;
import fa.training.cms.security.core.impl.LocalTokenResolver;
import fa.training.cms.service.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)
                .headers(header -> header.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("error").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("*/api-docs*/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post/{id}").permitAll()
                        .requestMatchers("/api/auth/seed").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/user/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/api/post/**").hasAnyAuthority(Role.ADMIN.name(), Role.EDITOR.name())
                        .requestMatchers("/api/category/**").hasAuthority(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationFilter authenticationFilter(TokenResolver tokenResolver) throws Exception {
        return new AuthenticationFilter(tokenResolver);
    }

    @Bean
    TokenResolver tokenResolver() {
        return new LocalTokenResolver();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
