package com.delivery.config;

import com.delivery.security.CustomUserDetailsService;
import com.delivery.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas publicas de autenticacion
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Comercios y Productos
                        .requestMatchers(HttpMethod.GET, "/api/v1/comercios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/comercios/**").hasRole("ADMIN")

                        // Pedidos
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mis-pedidos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/disponibles").hasAnyRole("REPARTIDOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/{id}/estado").hasAnyRole("REPARTIDOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/{id}/cancelar").hasAnyRole("CLIENTE", "ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}