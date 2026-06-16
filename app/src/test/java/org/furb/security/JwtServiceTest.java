package org.furb.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.furb.enums.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Chave de 32 bytes (mínimo HS256), codificada em Base64
        String secret = Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes());
        jwtService = new JwtService(secret, 1_800_000L);
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
        JwtService instavel = new JwtService(secretCurto, 1_800_000L);

        assertThatThrownBy(() -> instavel.gerarToken("x"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }

    @Test
    void gerarToken_comTipo_gravaClaimDeTipo() {
        String token = jwtService.gerarToken("admin@teste.com", TipoUsuario.ADMIN);

        assertThat(jwtService.extrairTipo(token)).isEqualTo("ADMIN");
        assertThat(jwtService.extrairEmail(token)).isEqualTo("admin@teste.com");
    }

    @Test
    void extrairTipo_tokenSemClaim_retornaNull() {
        String token = jwtService.gerarToken("sem@tipo.com"); // sobrecarga antiga, sem tipo

        assertThat(jwtService.extrairTipo(token)).isNull();
    }

    @Test
    void gerarToken_expiraConformeTtlConfigurado() {
        JwtService service = new JwtService(
                "7WR/z7mNy/4SMGg0fmc5i79b5EX8BsTJGaAASswJUio=", 1_800_000L);
        String token = service.gerarToken("user@x.com", TipoUsuario.CONSUMIDOR);

        Date exp = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode("7WR/z7mNy/4SMGg0fmc5i79b5EX8BsTJGaAASswJUio=")))
                .build().parseClaimsJws(token).getBody().getExpiration();

        long deltaMs = exp.getTime() - System.currentTimeMillis();
        // lower bound allows up to ~100s of test/exec slack
        assertThat(deltaMs).isBetween(1_700_000L, 1_800_000L);
    }
}
