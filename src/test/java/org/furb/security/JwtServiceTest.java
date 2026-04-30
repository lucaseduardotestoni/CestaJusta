package org.furb.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Chave de 32 bytes (mínimo HS256), codificada em Base64
        String secret = Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes());
        jwtService = new JwtService(secret);
    }

    @Test
    void gerarToken_retornaTokenNaoVazio() {
        String token = jwtService.gerarToken("lucas@teste.com");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    void extrairEmail_tokenValido_retornaEmailDoSubject() {
        String token = jwtService.gerarToken("lucas@teste.com");

        String email = jwtService.extrairEmail(token);

        assertThat(email).isEqualTo("lucas@teste.com");
    }

    @Test
    void validarToken_tokenValido_retornaTrue() {
        String token = jwtService.gerarToken("lucas@teste.com");

        assertThat(jwtService.validarToken(token)).isTrue();
    }

    @Test
    void validarToken_tokenAdulterado_retornaFalse() {
        String token = jwtService.gerarToken("lucas@teste.com");
        String tokenAdulterado = token.substring(0, token.length() - 4) + "AAAA";

        assertThat(jwtService.validarToken(tokenAdulterado)).isFalse();
    }

    @Test
    void gerarToken_secretMuitoCurto_lancaIllegalState() {
        // Base64 de "curto" = 5 bytes decodificados, abaixo dos 32 exigidos pelo HS256
        String secretCurto = Base64.getEncoder().encodeToString("curto".getBytes());
        JwtService instavel = new JwtService(secretCurto);

        assertThatThrownBy(() -> instavel.gerarToken("x"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }
}