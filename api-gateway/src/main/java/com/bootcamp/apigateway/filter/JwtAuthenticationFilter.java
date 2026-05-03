package com.bootcamp.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/user/signin",
            "/api/user/signup",
            "/api/stock/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretKey key;

    public JwtAuthenticationFilter(@Value("${app.jwt.secret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (HttpMethod.OPTIONS.matches(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String path = httpRequest.getRequestURI();
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (isPublicProductRequest(httpRequest.getMethod(), path)) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractBearerToken(httpRequest);
        if (token == null) {
            writeUnauthorized(httpResponse, "Missing or invalid Authorization header", path);
            return;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            Object roleClaim = claims.get("role");
            String role = roleClaim != null ? roleClaim.toString() : "USER";

            HeaderAddingRequestWrapper wrapped = new HeaderAddingRequestWrapper(httpRequest);
            wrapped.putHeader("X-User-Username", username);
            wrapped.putHeader("X-User-Role", role);

            chain.doFilter(wrapped, response);
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            writeUnauthorized(httpResponse, "Invalid or expired token", path);
        }
    }

    private boolean isPublicPath(String path) {
        for (String pattern : PUBLIC_PATHS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPublicProductRequest(String method, String path) {
        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }
        return pathMatcher.match("/api/product/**", path) || pathMatcher.match("/api/product", path);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", path);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
