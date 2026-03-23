package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/login", "/api/auth/register-customer").permitAll()
            
            // Phân quyền dựa trên Role trong Token
    .requestMatchers("/api/auth/register-employee").hasAnyAuthority("ROLE_ADMIN","ADMIN")
    .requestMatchers("/api/auth/register-admin").permitAll()
            
    
    // Cấu hình cho Sản phẩm
    .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated() // Mọi user đăng nhập đều được xem
    .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN") // Chỉ Admin mới được xóa
    .requestMatchers(HttpMethod.POST, "/api/products/*/import").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.POST, "/api/orders/*/return").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers("/api/reports/**").hasAuthority("ROLE_ADMIN")
    .requestMatchers(HttpMethod.GET, "/api/customers").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
    .requestMatchers(HttpMethod.POST, "/api/orders/checkout").hasAuthority("ROLE_USER")
    .requestMatchers("/api/cart/**").hasAuthority("ROLE_USER")  
    // Nhân viên & Admin được sửa, xóa sản phẩm và xác nhận hóa đơn
    .requestMatchers(HttpMethod.PUT, "/api/order/*/paid").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    // Xem lịch sử nhập hàng (Stock History)
    .requestMatchers("/api/reports/stock-history").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    // Khách hàng được quyền yêu cầu trả hàng của chính mình
.requestMatchers(HttpMethod.POST, "/api/orders/*/return-request").hasAuthority("ROLE_USER")

// Chỉ Nhân viên/Admin mới được xác nhận trả hàng
.requestMatchers(HttpMethod.PUT, "/api/orders/return/*/confirm").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
    .anyRequest().authenticated()
            );
        return http.build();
    }
}   