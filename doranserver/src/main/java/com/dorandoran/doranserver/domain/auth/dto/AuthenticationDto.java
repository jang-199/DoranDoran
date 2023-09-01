package com.dorandoran.doranserver.domain.auth.dto;

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

    @Getter
    @Setter
    public static class TokenTest{

        private String refreshToken;
        private String limitTime;

        @Builder
        public TokenTest(String refreshToken, String limitTime) {
            this.refreshToken = refreshToken;
            this.limitTime = limitTime;
        }
    }
}
