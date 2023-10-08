package com.dorandoran.doranserver.domain.member.controller;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.dto.AccountDto;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.member.service.SignUp;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    SignUp signUpService;

    @MockBean
    MemberService memberService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkNickname() {
    }

    @Test
    @DisplayName("/api/registered")
    void checkRegisteredMember() throws Exception {
        //given
        final String url = "/api/registered";
        ObjectMapper objectMapper = new ObjectMapper();
        AccountDto.CheckRegisteredMember checkRegisteredMember = AccountDto.CheckRegisteredMember.builder()
                .email("test@gmail.com")
                .osType(OsType.Ios)
                .build();
        Member member = Member.builder()
                .email("test@gmail.com")
                .nickname("nickname")
                .osType(OsType.Ios)
                .build();
//        Mockito.when(memberService.findByEmail("test@gmail.com")).thenThrow(new RuntimeException()).thenReturn(member);
        BDDMockito.given(memberService.findByEmail("test@gmail.com")).willThrow(new RuntimeException()).willReturn(member);

        //when
        ResultActions notFoundMemberExceptionResult = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkRegisteredMember)));

        ResultActions successResult = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkRegisteredMember)));

        //then
        notFoundMemberExceptionResult.andExpect(MockMvcResultMatchers.status().isBadRequest());
        successResult.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$['email']").value("test@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$['nickname']").value("nickname"));

    }

    @Test
    void changeNickname() {
    }


    @Test
    @DisplayName("/api/member")
    void signUp() throws Exception {
        //given
        final String url = "/api/member";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
//        Mockito.when(signUpService.getEmailByKakaoResourceServer(Mockito.anyString())).thenReturn("test@gmail.com");
        BDDMockito.given(signUpService.getEmailByKakaoResourceServer(Mockito.anyString())).willReturn("test1133@gmail.com");
        BDDMockito.given(memberService.findByEmilIsEmpty("test1133@gmail.com")).willReturn(true);
        AccountDto.SignUp signUp = AccountDto.SignUp.builder()
                .dateOfBirth(LocalDate.now())
                .nickname("TestNick")
                .firebaseToken("firebasetoken")
                .kakaoAccessToken("kakaoAccesstoken")
                .osType(OsType.Ios)
                .build();

        //when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUp)));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$['email']").value("test1133@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$['nickname']").value("TestNick"));

    }
}