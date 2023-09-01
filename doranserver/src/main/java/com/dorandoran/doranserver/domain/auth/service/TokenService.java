package com.dorandoran.doranserver.domain.auth.service;

import com.dorandoran.doranserver.domain.member.service.MemberServiceImpl;
import com.dorandoran.doranserver.global.config.jwt.TokenProvider;
import com.dorandoran.doranserver.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Period;
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberServiceImpl memberService;

    public String createNewAccessToken(String refreshToken) {
        log.info("createNewAccessToken");
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }

        Member member = memberService.findByRefreshToken(refreshToken);
        return tokenProvider.generateAccessToken(member, Duration.ofDays(1));
    }

    public String createNewRefreshToken(String refreshToken) {
        log.info("createNewRefreshToken");
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }
        Member member = memberService.findByRefreshToken(refreshToken);

        String newRefreshToken = tokenProvider.generateRefreshToken(member, Period.ofMonths(6));

        member.setRefreshToken(newRefreshToken);

        return newRefreshToken;
    }



}
