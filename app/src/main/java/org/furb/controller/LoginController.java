package org.furb.controller;

import jakarta.validation.Valid;
import org.furb.dto.usuario.LoginDTO;
import org.furb.model.Usuario;
import org.furb.security.CookieFactory;
import org.furb.security.JwtService;
import org.furb.services.LoginService;
import org.furb.services.RefreshTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieFactory cookieFactory;

    public LoginController(LoginService loginService,
                           JwtService jwtService,
                           RefreshTokenService refreshTokenService,
                           CookieFactory cookieFactory) {
        this.loginService = loginService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cookieFactory = cookieFactory;
    }

    @PostMapping
    public ResponseEntity<Void> login(@Valid @RequestBody LoginDTO dto) {
        Usuario usuario = loginService.autenticar(dto);
        String access = jwtService.gerarToken(usuario.getEmail(), usuario.getTipoUsuario());
        String refresh = refreshTokenService.emitir(usuario);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.access(access).toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.refresh(refresh).toString())
                .build();
    }
}
