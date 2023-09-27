package com.dorandoran.doranserver.domain.auth.service;

import com.dorandoran.doranserver.domain.member.service.MemberServiceImpl;
import com.dorandoran.doranserver.global.config.jwt.TokenProvider;
import com.dorandoran.doranserver.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Period;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberServiceImpl memberService;

    public String createNewAccessTokenWithRejectTime(String refreshToken, Date rejectExpiryDate) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }

        Member member = memberService.findByRefreshToken(refreshToken);
        return tokenProvider.generateNotificationRejectUserAccessToken(member, rejectExpiryDate);
    }

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }

        Member member = memberService.findByRefreshToken(refreshToken);
        return tokenProvider.generateAccessToken(member);
    }

    public String createNewRefreshToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected RefreshToken");
        }
        Member member = memberService.findByRefreshToken(refreshToken);

        String newRefreshToken = tokenProvider.generateRefreshToken(member);

        member.setRefreshToken(newRefreshToken);

        return newRefreshToken;
    }

    public boolean hasRejectTimeInClaim(String token) {
        Date rejectTime = tokenProvider.getRejectTime(token);
        Date now = new Date();
        return rejectTime != null && !rejectTime.before(now);
    }



}
