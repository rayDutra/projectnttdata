package com.nttdata.infrastructure.security;

public class TokenJWT {

    private String Token;

    public TokenJWT() {
    }
    public TokenJWT(String token) {
        Token = token;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

}
