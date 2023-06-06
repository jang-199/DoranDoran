package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Period;
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberServiceImpl memberService;

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }

        Member member = memberService.findByRefreshToken(refreshToken);
        return tokenProvider.generateAccessToken(member, Duration.ofDays(1));
    }

    public String createNewRefreshToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }
        Member member = memberService.findByRefreshToken(refreshToken);

        String newRefreshToken = tokenProvider.generateRefreshToken(member, Period.ofMonths(6));

        member.setRefreshToken(newRefreshToken);

        return newRefreshToken;
    }



}
