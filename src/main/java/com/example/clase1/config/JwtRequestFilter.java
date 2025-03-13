package com.example.clase1.config;

import com.example.clase1.services.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}") // Lee la clave secreta desde application.properties
    private String SECRET_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // Si no hay header o no es Bearer, pasa al siguiente filtro
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Extraer el token
        String token = authorizationHeader.substring(7);

        try {
            // Decodificar el encabezado del token para verificar el algoritmo
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                System.out.println("Token inválido: formato incorrecto.");
                chain.doFilter(request, response);
                return;
            }

            String headerJson = new String(Base64.getDecoder().decode(tokenParts[0]));
            JSONObject header = new JSONObject(headerJson);
            String alg = header.optString("alg");

            // Procesar solo tokens HS256
            if ("HS256".equals(alg)) {
                System.out.println("Token JWT clásico (HS256) detectado.");
                Claims claims = validateAndParseHs256Token(token);
                processAuthentication(claims, token, request);
            } else {
                System.out.println("Token no es HS256 (probablemente OAuth2/RS256), delegando a Spring Security.");
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el token: " + e.getMessage());
            // Si hay un error (token malformado, etc.), pasa al siguiente filtro
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    // Método para validar y parsear token HS256
    private Claims validateAndParseHs256Token(String token) {
        byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Jwts.parser()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método para procesar la autenticación
    private void processAuthentication(Claims claims, String token, HttpServletRequest request) {
        String username = claims.getSubject();

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token);
                System.out.println("Roles asignados al usuario: " + authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Autenticación establecida para: " + username);
            } else {
                System.out.println("Token inválido o no coincide con el usuario.");
            }
        }
    }
}