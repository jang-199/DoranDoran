package com.dorandoran.doranserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthenticationDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TokenResponse{
        private String refreshToken;
        private String accessToken;

        @Builder
        public TokenResponse(String refreshToken, String accessToken) {
            this.refreshToken = refreshToken;
            this.accessToken = accessToken;
        }
    }
}
