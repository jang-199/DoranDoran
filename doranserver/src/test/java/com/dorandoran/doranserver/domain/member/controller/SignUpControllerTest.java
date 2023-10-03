package com.dorandoran.doranserver.domain.member.controller;

import com.dorandoran.doranserver.domain.auth.service.TokenService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
import com.dorandoran.doranserver.domain.member.dto.AccountDto;
import com.dorandoran.doranserver.domain.member.repository.MemberRepository;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.member.service.PolicyTermsCheck;
import com.dorandoran.doranserver.domain.member.service.SignUp;
import com.dorandoran.doranserver.domain.member.service.SignUpImpl;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class SignUpControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    private final String refreshToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E";
    @MockBean
    private SignUp signUpService;
    @MockBean
    private MemberService memberService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /*@Test
    @Transactional
    void checkNickname_integration() throws Exception {
        PolicyTerms build3 = PolicyTerms.builder().policy1(true).policy2(true).policy3(true).build();
        policyTermsCheck.policyTermsSave(build3);
        Member build1 = Member.builder()
                .policyTermsId(build3)
                .email("9643us@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000,12,12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();
        memberService.saveMember(build1);

        log.info("비속어 필터링");
        String slangNickname = new ObjectMapper().writeValueAsString(new AccountDto.CheckNickname("시발"));
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/nickname")
                        .header("Authorization",refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(slangNickname))

                .andExpect(status().is(422));

        log.info("글자 수 넘어감");
        String indexOutOfNickname = new ObjectMapper().writeValueAsString(new AccountDto.CheckNickname("도란도란화이팅입니다"));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/nickname")
                                .header("Authorization",refreshToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(indexOutOfNickname))

                .andExpect(status().is(422));

        log.info("중복된 닉네임");
        String conflictNickname = new ObjectMapper().writeValueAsString(new AccountDto.CheckNickname("doran"));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/nickname")
                                .header("Authorization",refreshToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(conflictNickname))

                .andExpect(status().is(409));

        log.info("사용 가능 닉네임");
        String notConflictNickname = new ObjectMapper().writeValueAsString(new AccountDto.CheckNickname("akdd"));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/nickname")
                                .header("Authorization",refreshToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(notConflictNickname))

                .andExpect(status().is(204));
    }*/

    @Test
    @DisplayName("닉네임 중복체크")
    void checkNickname() throws Exception{
        //given
        String nickname = new ObjectMapper().writeValueAsString(new AccountDto.CheckNickname("akdd"));

//        Mockito.when(signUpService.existedNickname(Mockito.any())).thenReturn(Boolean.TRUE).thenReturn(Boolean.FALSE);

        //when
        ResultActions resultActions_True = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nickname));

        ResultActions resultActions_False = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nickname));

        //then
        resultActions_True
                .andExpect(status().is(409));

        resultActions_False
                .andExpect(status().is(204));
    }

    @Test
    void checkRegisteredMember() {
    }

    @Test
    void changeNickname() throws Exception {
        //given
        String changeNickname = "akdd";
        String nickname = new ObjectMapper().writeValueAsString(new AccountDto.CheckNickname(changeNickname));
        Member member = Member.builder()
                .email("9643us@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000,12,12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();

        Mockito.when(signUpService.existedNickname(Mockito.any())).thenReturn(Boolean.TRUE).thenReturn(Boolean.FALSE);
        Mockito.when(memberService.findByEmail(Mockito.any())).thenReturn(member);
        Mockito.doNothing().when(memberService).setNickname(member, changeNickname);

        //when
        ResultActions resultActions_True = mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/nickname")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nickname));

        ResultActions resultActions_False = mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/nickname")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nickname));

        //then
        resultActions_True
                .andExpect(status().is(409));

        resultActions_False
                .andExpect(status().is(204));
    }

    @Test
    void signUp() {

    }
}