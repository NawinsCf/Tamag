package com.tamago.tamagoservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        // allow localhost with any port during development
        .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
        // allow requests from the deployed front (public IP or domain)
        // NOTE: consider restricting this to your real domain in production
        .allowedOriginPatterns("http://13.39.79.78", "http://13.39.79.78:3000", "https://13.39.79.78", "https://13.39.79.78:3000")
        // as a fallback allow any origin pattern (use with caution)
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
    }
}
