package com.Qomoi1.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CORSConfiguration {

    @Value("${app.filter.cors-filter.allowed-origins}")
    String corsFilterAllowedOrigins;

    @Value("${app.filter.cors-filter.allowed-methods}")
    String corsFilterAllowedMethods;

    @Value("${app.filter.cors-filter.allowed-headers}")
    String corsFilterAllowedHeaders;

    @Value("${app.filter.cors-filter.allowed-path-pattern}")
    String corsFilterAllowedPattern;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(delimitedStringToList(corsFilterAllowedOrigins));
        corsConfig.setAllowedHeaders(delimitedStringToList(corsFilterAllowedHeaders));
        corsConfig.setAllowedMethods(delimitedStringToList(corsFilterAllowedMethods));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }

    private List<String> delimitedStringToList(String allowedList) {
        return Arrays.asList(allowedList.split(","));
    }
}
