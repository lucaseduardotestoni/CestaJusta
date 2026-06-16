package org.furb.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.furb.security.CookieFactory;
import org.furb.services.RefreshTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final CookieFactory cookieFactory;

    public LogoutController(RefreshTokenService refreshTokenService, CookieFactory cookieFactory) {
        this.refreshTokenService = refreshTokenService;
        this.cookieFactory = cookieFactory;
    }

    @PostMapping
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshCru = lerCookie(request, CookieFactory.REFRESH);
        if (refreshCru != null) {
            refreshTokenService.revogarTokenCru(refreshCru);
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearAccess().toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearRefresh().toString())
                .build();
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
