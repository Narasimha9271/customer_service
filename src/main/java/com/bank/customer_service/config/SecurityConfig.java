package com.bank.customer_service.config;

import com.bank.customer_service.security.CustomerDetailsService;
import com.bank.customer_service.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register", "/auth/generateToken").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/accounts/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/accounts/me").hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyRole("ADMIN", "CUSTOMER")

                        .requestMatchers(HttpMethod.GET, "/api/transactions/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/transactions/**").hasAnyRole("ADMIN", "CUSTOMER")

                        .requestMatchers(HttpMethod.POST, "/api/customers/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.PUT, "/api/customers/me/change-email").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.PUT, "/api/customers/me/change-password").hasRole("CUSTOMER")

                                .requestMatchers(HttpMethod.PUT, "/api/customers/me").hasRole("CUSTOMER")


                        .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
