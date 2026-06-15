package org.furb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
                || "/usuarios/cadastro".equals(uri)
                || uri.startsWith("/login/")
                // STOPGAP (remover ao migrar p/ cookie httpOnly): imagens de produto servidas sem token,
                // pois a <img> do navegador não envia o Bearer. Ver memória auth-hardening.
                || uri.startsWith("/uploads/produtos/");
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
}
