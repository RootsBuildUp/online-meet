package com.ticonsys.online_meet.dto;

public record AuthenticationResponse (String tokenType,String accessToken,String refreshToken){

    public AuthenticationResponse(String tokenType, String accessToken) {
        this(tokenType, accessToken, null);
    }

    public AuthenticationResponse(String tokenType, String accessToken, String refreshToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
