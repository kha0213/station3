package com.yl.station3.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String name;

    public JwtResponse(String accessToken, String email, String name) {
        this.accessToken = accessToken;
        this.email = email;
        this.name = name;
    }
}
