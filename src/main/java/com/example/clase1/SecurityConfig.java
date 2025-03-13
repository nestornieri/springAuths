package com.example.clase1;

import com.example.clase1.config.JwtRequestFilter;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    public JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesiones
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/authenticate").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Endpoints autenticados genéricos
                        .requestMatchers("/allDTO", "/listarProductos").authenticated()
                        // Endpoints con autoridad ROLE_ADMIN
                        .requestMatchers(HttpMethod.POST, "/addProducto").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api1/admin", "/api1/privado").hasAuthority("ROLE_ADMIN")
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                // Filtro JWT personalizado primero
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                // Autenticación básica
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciales inválidas");
                        })
                )
                // OAuth2 Resource Server
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(request -> authentication -> {
                            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                            if (existingAuth != null && existingAuth.isAuthenticated()) {
                                return existingAuth;
                            }
                            try {
                                JwtDecoder jwtDecoder = jwtDecoder();
                                String token = ((BearerTokenAuthenticationToken) authentication).getToken();
                                Jwt jwt = jwtDecoder.decode(token);
                                List<GrantedAuthority> authorities;
                                if (jwt.getClaimAsStringList("roles") != null) {
                                    authorities = jwt.getClaimAsStringList("roles")
                                            .stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());
                                } else {
                                    authorities = jwt.getClaimAsStringList("scope")
                                            .stream()
                                            .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                                            .collect(Collectors.toList());
                                }
                                System.out.println("Claims del token: " + jwt.getClaims());
                                System.out.println("Autoridades asignadas: " + authorities);
                                return new JwtAuthenticationToken(jwt, authorities);
                            } catch (JwtException e) {
                                System.out.println("Error al validar token OAuth2: " + e.getMessage());
                                throw new BadCredentialsException("Token OAuth2 inválido", e);
                            }
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error de autenticación OAuth2: " + authException.getMessage());
                        })
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/oauth2/jwks").build();
    }



    /*@Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("passadmin"))
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }*/

}