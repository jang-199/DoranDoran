package com.dorandoran.doranserver.domain.auth.controller;

import com.dorandoran.doranserver.domain.auth.dto.AuthenticationDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.global.config.jwt.TokenProvider;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

//    @Test
//    @DisplayName("/api/token")
//    void tokenCheck() {
//        Jwts.builder()
//                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
//                .setIssuer("tester")
//                .setIssuedAt(new Date())
//                .setExpiration(expiry)
//                .setSubject(user.getNickname())
//                .claim("ROLE","ROLE_USER")
//                .claim("email",user.getEmail())
//                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
//                .compact();
//        //given
//        AuthenticationDto.TokenResponse tokenResponse = AuthenticationDto.TokenResponse.builder()
//                .accessToken("acc")
//                .refreshToken("ref")
//                .build();
//        Member member = Member.builder().email("test@email.com").build();
//        //todo 엑세스 리프레시 모든 경우의 수
//        //a 0, r 0
//        String acc = tokenProvider.generateAccessToken(member);
//        BDDMockito.given(tokenProvider)
//        //a 0, r 0 만료직전
//        //a 0, r x
//        //a x, r 0
//        //a x, r x
//
//        //when
//
//        //then
//    }
}