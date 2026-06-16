package org.furb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieFactory {

    public static final String ACCESS = "cj_token";
    public static final String REFRESH = "cj_refresh";

    private final boolean secure;
    private final long accessTtlMs;
    private final long refreshTtlMs;

    public CookieFactory(@Value("${app.auth.cookie.secure:false}") boolean secure,
                         @Value("${jwt.access-ttl-ms:1800000}") long accessTtlMs,
                         @Value("${app.auth.refresh-ttl-ms:28800000}") long refreshTtlMs) {
        this.secure = secure;
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
    }

    public ResponseCookie access(String jwt) {
        return base(ACCESS, jwt, Duration.ofMillis(accessTtlMs));
    }

    public ResponseCookie refresh(String raw) {
        return base(REFRESH, raw, Duration.ofMillis(refreshTtlMs));
    }

    public ResponseCookie clearAccess() {
        return base(ACCESS, "", Duration.ZERO);
    }

    public ResponseCookie clearRefresh() {
        return base(REFRESH, "", Duration.ZERO);
    }

    private ResponseCookie base(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
