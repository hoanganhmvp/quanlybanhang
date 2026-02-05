package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
     @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

 @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable()) 
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**", "/gen-token").permitAll()
    
    .requestMatchers(HttpMethod.POST, "/api/products", "/api/products/").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    
    .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyAuthority("ROLE_ADMIN")
    
    .requestMatchers(HttpMethod.POST, "/api/orders", "/api/orders/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_EMPLOYEE") 
    .requestMatchers("/api/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.PUT, "/api/orders/*/paid").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
    .requestMatchers(HttpMethod.POST, "/api/orders/*/return").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers("/api/vouchers/**").hasAuthority("ROLE_ADMIN")
    // B. Cả ADMIN và NHÂN VIÊN được xem danh sách khách hàng
    .requestMatchers(HttpMethod.GET, "/api/customers").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .anyRequest().authenticated()
    // Trong SecurityConfig.java, phần authorizeHttpRequests
)
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
    return http.build();
}
}






