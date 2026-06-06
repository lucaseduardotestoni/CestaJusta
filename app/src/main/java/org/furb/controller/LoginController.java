package org.furb.controller;

import jakarta.validation.Valid;
import org.furb.dto.usuario.LoginDTO;
import org.furb.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {this.loginService = loginService;}

    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO dto) {
        String token = loginService.login(dto);
        return ResponseEntity.ok(token);
    }

}
