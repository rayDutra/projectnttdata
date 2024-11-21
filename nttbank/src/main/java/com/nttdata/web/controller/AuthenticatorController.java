package com.nttdata.web.controller;

import com.nttdata.domain.entity.User;
import com.nttdata.dto.UserDTO;
import com.nttdata.infrastructure.security.TokenJWT;
import com.nttdata.infrastructure.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticatorController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody UserDTO userDTO) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getLogin(), userDTO.getPassword());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new TokenJWT(tokenJWT));
    }

}
