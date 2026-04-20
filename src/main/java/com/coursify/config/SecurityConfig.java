package com.coursify.config;

import com.coursify.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers("/api/lessons/**").hasAnyRole("TEACHER", "ADMIN", "STUDENT")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://cousify.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests(auth -> auth
//                        // ── Swagger / OpenAPI ───────────────────────────────
//                        // Stateless mean api not pass long term that need to login again if reload
//                        .requestMatchers(
//                                "/swagger-ui.html",
//                                "/swagger-ui/**",
//                                "/v3/api-docs",
//                                "/v3/api-docs/**",
//                                "/v3/api-docs.yaml",
//                                "/swagger-resources",
//                                "/swagger-resources/**",
//                                "/webjars/**"
//                        ).permitAll()
//
//                        // ── Authentication endpoints ───────────────────────
//                        .requestMatchers("/api/auth/**").permitAll()
//
//                        // ── Public GET endpoints ──────────────────────────
//                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
//
//                        // ── Current user endpoint ─────────────────────────
//                        .requestMatchers("/api/users/me").authenticated()
//
//                        // ── Admin only endpoints ─────────────────────────
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // ── Teacher/Admin endpoints ──────────────────────
//                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
//
//                        // ── Lessons accessible by Teacher/Admin/Student ──
//                        .requestMatchers("/api/lessons/**").hasAnyRole("TEACHER", "ADMIN", "STUDENT")
//
//                        // ── All other requests require authentication ────
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(List.of("*"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}