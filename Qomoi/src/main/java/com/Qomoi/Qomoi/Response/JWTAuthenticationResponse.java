package com.Qomoi.Qomoi.Response;

import lombok.Data;

@Data
public class JWTAuthenticationResponse {

    private String token;
    private String refreshToken;
}
