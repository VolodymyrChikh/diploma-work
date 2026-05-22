package com.volodymyrchikh.abitandstudhelp.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.volodymyrchikh.abitandstudhelp.security.auth.JwtAuthenticationFilter;
import com.volodymyrchikh.abitandstudhelp.service.UsersDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String DEFAULT_ALLOWED_ORIGIN_PATTERNS =
            "http://localhost:9000," +
                    "http://localhost:9001,"
                    + "http://localhost:3000,"
                    + "http://localhost:3001,"
                    + "http://localhost:5173,"
                    + "http://localhost:4173,"
                    + "http://127.0.0.1:3000,"
                    + "http://127.0.0.1:3001,"
                    + "http://127.0.0.1:5173,"
                    + "http://192.168.*.*:3000,"
                    + "http://192.168.*.*:3001,"
                    + "http://192.168.*.*:5173,"
                    + "http://10.*.*.*:3000,"
                    + "http://10.*.*.*:3001,"
                    + "http://172.*.*.*:3000,"
                    + "http://172.*.*.*:3001,"
                    + "https://*.vercel.app,"
                    + "https://*.run.app";

    private final UsersDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Value("${app.cors.allowed-origin-patterns:" + DEFAULT_ALLOWED_ORIGIN_PATTERNS + "}")
    private String allowedOriginPatterns;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
                            problemDetail.setType(URI.create("about:blank"));
                            problemDetail.setTitle("Доступ заборонено");
                            problemDetail.setInstance(URI.create(request.getRequestURI()));
                            problemDetail.setDetail("Недостатньо прав для цієї дії");

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write(new ObjectMapper().writeValueAsString(problemDetail));
                        }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/signup",
                                "/signin",
                                "/main",
                                "/about-specialties",
                                "/course-map",
                                "/forum",
                                "/forum/post/*",
                                "/post/*",
                                "/media",
                                "/create-post",
                                "/profile",
                                "/api/file-categories",
                                "/api/file-categories/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/posts/**",
                                "/comments/**",
                                "/categories/**",
                                "/specialties/**",
                                "/subjects/**",
                                "/faqs/**",
                                "/api/media/**",
                                "/api/schedule/**"
                        ).permitAll()
                        .requestMatchers("/ai/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/media/*/increment-views").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories/**", "/specialties/**", "/subjects/**", "/faqs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**", "/specialties/**", "/subjects/**", "/faqs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**", "/specialties/**", "/subjects/**", "/faqs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/*/avatar").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers(HttpMethod.DELETE, "/comments/*/by-rules").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/posts/**", "/comments/**", "/api/media/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers(HttpMethod.PUT, "/posts/**", "/comments/**", "/api/media/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers(HttpMethod.DELETE, "/posts/**", "/comments/**", "/api/media/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/notifications/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(splitCsv(allowedOriginPatterns));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> splitCsv(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }

}
