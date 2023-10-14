package com.dorandoran.doranserver.domain.auth.controller;

import com.dorandoran.doranserver.domain.auth.dto.AuthenticationDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.global.config.jwt.TokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @MockBean
    MemberService memberService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("/api/token")
    void tokenCheck() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDateTime pastAn1hourLater = LocalDateTime.now().minusHours(1);
        LocalDateTime futureInAn1hour = LocalDateTime.now().plusHours(1);
        LocalDateTime futureInAnSomeMonth = LocalDateTime.now().plusMonths(6);

        Date past = Date.from(pastAn1hourLater.atZone(ZoneId.systemDefault()).toInstant());
        Date future = Date.from(futureInAn1hour.atZone(ZoneId.systemDefault()).toInstant());
        Date longFuture = Date.from(futureInAnSomeMonth.atZone(ZoneId.systemDefault()).toInstant());

        String pastToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("jw1010110@gmail.com")
                .setIssuedAt(past)
                .setExpiration(past)
                .setSubject("9643us@naver.com")
                .claim("ROLE", "ROLE_USER")
                .claim("email", "9643us@naver.com")
                .signWith(Keys.hmacShaKeyFor("hjsdoran2023hjsdoran2023hjsdoran2023hjsdoran2023hjsdoran2023".getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        String futureToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("jw1010110@gmail.com")
                .setIssuedAt(new Date())
                .setExpiration(future)
                .setSubject("9643us@naver.com")
                .claim("ROLE", "ROLE_USER")
                .claim("email", "9643us@naver.com")
                .signWith(Keys.hmacShaKeyFor("hjsdoran2023hjsdoran2023hjsdoran2023hjsdoran2023hjsdoran2023".getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
        String longFutureToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("jw1010110@gmail.com")
                .setIssuedAt(new Date())
                .setExpiration(longFuture)
                .setSubject("9643us@naver.com")
                .claim("ROLE", "ROLE_USER")
                .claim("email", "9643us@naver.com")
                .signWith(Keys.hmacShaKeyFor("hjsdoran2023hjsdoran2023hjsdoran2023hjsdoran2023hjsdoran2023".getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        //given
        BDDMockito.given(memberService.findByRefreshToken(BDDMockito.anyString())).willReturn(Member.builder().email("test@gmail.com").build());
        AuthenticationDto.TokenResponse accessFutureRefreshLongFuture = AuthenticationDto.TokenResponse.builder()
                .accessToken(futureToken)
                .refreshToken(longFutureToken)
                .build();

        AuthenticationDto.TokenResponse accessFutureRefreshFuture = AuthenticationDto.TokenResponse.builder()
                .accessToken(futureToken)
                .refreshToken(futureToken)
                .build();

        AuthenticationDto.TokenResponse accessFutureRefreshPast = AuthenticationDto.TokenResponse.builder()
                .accessToken(futureToken)
                .refreshToken(pastToken)
                .build();

        AuthenticationDto.TokenResponse accessPastRefreshLongFuture = AuthenticationDto.TokenResponse.builder()
                .accessToken(pastToken)
                .refreshToken(longFutureToken)
                .build();

        AuthenticationDto.TokenResponse accessPastRefreshFuture = AuthenticationDto.TokenResponse.builder()
                .accessToken(pastToken)
                .refreshToken(futureToken)
                .build();

        AuthenticationDto.TokenResponse accessPastTokenRefreshPastToken = AuthenticationDto.TokenResponse.builder()
                .accessToken(pastToken)
                .refreshToken(pastToken)
                .build();





        //when
        ResultActions accessFutureRefreshLongFutureResults = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessFutureRefreshLongFuture))
        );

        ResultActions accessFutureRefreshFutureResults = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessFutureRefreshFuture))
        );

        ResultActions accessFutureRefreshPastResults = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessFutureRefreshPast))
        );

        ResultActions accessPastRefreshLongFutureResults = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessPastRefreshLongFuture))
        );

        ResultActions accessPastRefreshFutureResults = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessPastRefreshFuture))
        );

        ResultActions accessPastTokenRefreshPastTokenResults = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessPastTokenRefreshPastToken))
        );

        //then accessToken

        accessFutureRefreshLongFutureResults.andExpect(MockMvcResultMatchers.status().isNoContent());
        accessFutureRefreshFutureResults.andExpect(MockMvcResultMatchers.status().isNoContent());
        accessFutureRefreshPastResults.andExpect(MockMvcResultMatchers.status().isNoContent());
        accessPastRefreshLongFutureResults.andExpect(MockMvcResultMatchers.jsonPath("$['refreshToken']").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$['accessToken']").exists());
        accessPastRefreshFutureResults.andExpect(MockMvcResultMatchers.jsonPath("$['refreshToken']").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$['accessToken']").exists());
        accessPastTokenRefreshPastTokenResults.andExpect(MockMvcResultMatchers.status().isForbidden());

    }
}