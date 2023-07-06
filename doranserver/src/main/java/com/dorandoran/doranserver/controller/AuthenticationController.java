package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.dto.TokenDto;
import com.dorandoran.doranserver.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    //Todo 리프레시 토큰과 엑세스 토큰을 받아 엑세스 토큰이 만료되었고 리프레시 토큰이 유효하면 새로운 엑세스 토큰 발급 메서드 (이때 리프레시 토큰이 x기간 이하로 남았으면 리프레시 토큰도 새로 발급)
    @PostMapping("/token")
    ResponseEntity<?> tokenCheck(@RequestBody TokenDto tokenDto) {

        TokenDto responseTokenDto = new TokenDto();

        if (!tokenProvider.validToken(tokenDto.getAccessToken()) && !tokenProvider.validToken(tokenDto.getRefreshToken())) { //Access & Refresh 유효 x
            if (tokenProvider.isExpired(tokenDto.getAccessToken()) && tokenProvider.isExpired(tokenDto.getRefreshToken())) { //Access & Refresh 둘 다 만료
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        if (!tokenProvider.validToken(tokenDto.getAccessToken()) && tokenProvider.validToken(tokenDto.getRefreshToken())) { //Access 유효 x & Refresh 유효
            if (tokenProvider.isExpired(tokenDto.getAccessToken())) { //Access 만료
                log.info("Access 발급");
                String newAccessToken = tokenService.createNewAccessToken(tokenDto.getRefreshToken()); //AccessToken 발급
                responseTokenDto.setAccessToken(newAccessToken);

                //refresh token expired check
                if (tokenProvider.getExpiryDuration(tokenDto.getRefreshToken()).compareTo(Duration.ofDays(21)) < 0) { //21일 보다 만료 기간이 작음
                    log.info("Refresh 발급");
                    String newRefreshToken = tokenService.createNewRefreshToken(tokenDto.getRefreshToken()); //RefreshToken 발급
                    responseTokenDto.setRefreshToken(newRefreshToken);
                }
            }
        }

        if (responseTokenDto.getAccessToken() != null) {
            return ResponseEntity.ok().body(responseTokenDto);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
