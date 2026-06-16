package org.furb.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.furb.model.Usuario;
import org.furb.security.CookieFactory;
import org.furb.security.JwtService;
import org.furb.services.RefreshTokenService;
import org.furb.services.exeptions.BusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh")
public class RefreshController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final CookieFactory cookieFactory;

    public RefreshController(RefreshTokenService refreshTokenService,
                             JwtService jwtService,
                             CookieFactory cookieFactory) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.cookieFactory = cookieFactory;
    }

    @PostMapping
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        String refreshCru = lerCookie(request, CookieFactory.REFRESH);
        if (refreshCru == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            RefreshTokenService.Rotacao r = refreshTokenService.rotacionar(refreshCru);
            Usuario u = r.usuario();
            String access = jwtService.gerarToken(u.getEmail(), u.getTipoUsuario());
            return ResponseEntity.noContent()
                    .header(HttpHeaders.SET_COOKIE, cookieFactory.access(access).toString())
                    .header(HttpHeaders.SET_COOKIE, cookieFactory.refresh(r.novoTokenCru()).toString())
                    .build();
        } catch (BusinessException e) {
            return ResponseEntity.status(401)
                    .header(HttpHeaders.SET_COOKIE, cookieFactory.clearAccess().toString())
                    .header(HttpHeaders.SET_COOKIE, cookieFactory.clearRefresh().toString())
                    .build();
        }
    }

    private String lerCookie(HttpServletRequest request, String nome) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (nome.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                return c.getValue();
            }
        }
        return null;
    }
}
