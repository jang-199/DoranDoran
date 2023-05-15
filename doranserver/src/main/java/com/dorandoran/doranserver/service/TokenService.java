package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.entity.Member;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberServiceImpl memberService;

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }

        Member member = memberService.findByRefreshToken(refreshToken);
        return tokenProvider.generateToken(member, Duration.ofHours(12));
    }
}
