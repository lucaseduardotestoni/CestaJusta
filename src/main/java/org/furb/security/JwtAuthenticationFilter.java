package org.furb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getServletPath();
        String method = request.getMethod();
        return "OPTIONS".equalsIgnoreCase(method)
                || "/login".equals(uri)
                || uri.startsWith("/login/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("JwtAuthenticationFilter - Processando request para: {}", path);
        
        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            logger.debug("Authorization header: {}", header == null ? "null" : "presente");

            if (header == null || !header.startsWith("Bearer ")) {
                logger.warn("Requisição sem Bearer token: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
                return;
            }

            String token = header.substring(7);
            logger.debug("Token extraído (primeiros 20 chars): {}", token.substring(0, Math.min(20, token.length())));

            if (!jwtService.validarToken(token)) {
                logger.warn("Token inválido para: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
                return;
            }

            String email = jwtService.extrairEmail(token);
            logger.debug("Token validado para email: {}", email);
            
            var authentication = new UsernamePasswordAuthenticationToken(email, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Erro no JWT Authentication Filter", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
        }
    }
}
