package com.dorandoran.doranserver.domain.auth.controller;

import com.dorandoran.doranserver.domain.auth.dto.AuthenticationDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.repository.MemberRepository;
import com.dorandoran.doranserver.global.config.jwt.TokenProvider;
import com.dorandoran.doranserver.domain.auth.service.TokenService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@Timed
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @PatchMapping("/token")
    ResponseEntity<?> tokenCheck(@RequestBody AuthenticationDto.TokenResponse tokenDto) {
        log.info("tokenCheck api 호출");
        AuthenticationDto.TokenResponse.TokenResponseBuilder builder = AuthenticationDto.TokenResponse.builder();

        if (!tokenProvider.validToken(tokenDto.getAccessToken()) && !tokenProvider.validToken(tokenDto.getRefreshToken())) { //Access & Refresh 유효 x
            if (tokenProvider.isExpired(tokenDto.getAccessToken()) && tokenProvider.isExpired(tokenDto.getRefreshToken())) { //Access & Refresh 둘 다 만료
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        if (!tokenProvider.validToken(tokenDto.getAccessToken()) && tokenProvider.validToken(tokenDto.getRefreshToken())) { //Access 유효 x & Refresh 유효
            if (tokenProvider.isExpired(tokenDto.getAccessToken())) { //Access 만료
                log.info("Access 발급");
                String newAccessToken = tokenService.createNewAccessToken(tokenDto.getRefreshToken()); //AccessToken 발급
                builder.accessToken(newAccessToken);
//                responseTokenDto.setAccessToken(newAccessToken);

                //refresh token expired check
                if (tokenProvider.getExpiryDuration(tokenDto.getRefreshToken()).compareTo(Duration.ofDays(21)) < 0) { //21일 보다 만료 기간이 작음
                    log.info("Refresh 발급");
                    String newRefreshToken = tokenService.createNewRefreshToken(tokenDto.getRefreshToken()); //RefreshToken 발급
                    builder.refreshToken(newRefreshToken);
//                    responseTokenDto.setRefreshToken(newRefreshToken);
                }
            }
        }
        AuthenticationDto.TokenResponse response = builder.build();

        if (response.getAccessToken() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/test/token")
    ResponseEntity<?> tokenCheckTest(@RequestBody AuthenticationDto.TokenTest tokenDto) {

        Optional<Member> byRefreshToken = memberRepository.findByRefreshToken(tokenDto.getRefreshToken());
        String s = tokenProvider.generateAccessToken(byRefreshToken.get(), Duration.ofMillis(Long.parseLong(tokenDto.getLimitTime())));
        return ResponseEntity.ok().body(s);
    }
}
