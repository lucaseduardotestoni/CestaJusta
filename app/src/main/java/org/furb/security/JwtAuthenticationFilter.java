package org.furb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.furb.model.Usuario;
import org.furb.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getServletPath();
        String method = request.getMethod();
        return "OPTIONS".equalsIgnoreCase(method)
                || "/login".equals(uri)
                || "/logout".equals(uri)
                || "/refresh".equals(uri)
                || "/usuarios/cadastro".equals(uri)
                || uri.startsWith("/login/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("JwtAuthenticationFilter - Processando request para: {}", path);
        
        try {
            String token = extrairToken(request);
            if (token == null) {
                logger.warn("Requisição sem token (cookie/header): {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
                return;
            }

            if (!jwtService.validarToken(token)) {
                logger.warn("Token inválido para: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
                return;
            }

            String email = jwtService.extrairEmail(token);
            logger.debug("Token validado para email: {}", email);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                logger.warn("Token válido mas usuário não existe mais: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
                return;
            }

            Usuario usuario = usuarioOpt.get();
            if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                logger.warn("Token válido mas usuário inativo: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario inativo");
                return;
            }

            SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_" + usuario.getTipoUsuario().name());
            var authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(role));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Erro no JWT Authentication Filter", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou invalido");
        }
    }

    private String extrairToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (CookieFactory.ACCESS.equals(cookie.getName())
                        && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
