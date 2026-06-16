package org.furb.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;

class CookieFactoryTest {

    @Test
    void access_montaCookieHttpOnlyStrict() {
        CookieFactory f = new CookieFactory(false, 1_800_000L, 28_800_000L);
        ResponseCookie c = f.access("jwt-fake");

        assertThat(c.getName()).isEqualTo("cj_token");
        assertThat(c.getValue()).isEqualTo("jwt-fake");
        assertThat(c.isHttpOnly()).isTrue();
        assertThat(c.getSameSite()).isEqualTo("Strict");
        assertThat(c.getPath()).isEqualTo("/");
        assertThat(c.getMaxAge().getSeconds()).isEqualTo(1800);
        assertThat(c.isSecure()).isFalse();
    }

    @Test
    void refresh_respeitaSecureETtl() {
        CookieFactory f = new CookieFactory(true, 1_800_000L, 28_800_000L);
        ResponseCookie c = f.refresh("opaco");

        assertThat(c.getName()).isEqualTo("cj_refresh");
        assertThat(c.isSecure()).isTrue();
        assertThat(c.getMaxAge().getSeconds()).isEqualTo(28800);
    }

    @Test
    void clear_zeraMaxAge() {
        CookieFactory f = new CookieFactory(false, 1_800_000L, 28_800_000L);
        assertThat(f.clearAccess().getMaxAge().getSeconds()).isZero();
        assertThat(f.clearRefresh().getMaxAge().getSeconds()).isZero();
        assertThat(f.clearAccess().getName()).isEqualTo("cj_token");
        assertThat(f.clearRefresh().getName()).isEqualTo("cj_refresh");
    }
}
