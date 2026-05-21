package com.nlmk.LAL.MobileGuns.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.nlmk.LAL.MobileGuns.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http
            .getSharedObject(AuthenticationManagerBuilder.class);
        return authBuilder.build();
    }

    @Bean
    ActiveDirectoryLdapAuthenticationProvider authenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider adProvider =
            new ActiveDirectoryLdapAuthenticationProvider(
                "duferco.be",
                "ldap://10.168.7.32:389"
            );

        adProvider.setConvertSubErrorCodesToExceptions(true);
        adProvider.setUseAuthenticationRequestCredentials(true);
        adProvider.setSearchFilter("(sAMAccountName={1})");

        return adProvider;
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("*");

        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers("/api/application/**").permitAll()
                
                .requestMatchers(
                    "/api/identification/**",
                    "/api/enlogement/**",
                    "/api/inventory/**"
                ).authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new CorsFilter(corsConfigurationSource()),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new JwtFilter(customUserDetailsService, jwtUtils),
                UsernamePasswordAuthenticationFilter.class
            )
            .build();
    }
}