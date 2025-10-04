package com.tamago.tamagoservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/api/*");
        reg.setName("jwtAuthFilter");
        reg.setOrder(1);
        return reg;
    }
}
