package com.dorandoran.doranserver.domain.member.controller;

import com.dorandoran.doranserver.domain.member.dto.AccountDto;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTest {
    @Autowired
    protected MockMvc mockMvc;

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
    void checkRegisteredMember() {
    }

    @Test
    void changeNickname() {
    }

    @Test
    void signUp() {
        //given
        final String url = "/api/member";

        ObjectMapper objectMapper = new ObjectMapper();
        AccountDto.SignUp signUp = AccountDto.SignUp.builder()
                .dateOfBirth(LocalDate.now())
                .nickname("TestNick")
                .firebaseToken("firebasetoken")
                .kakaoAccessToken("kakaoAccesstoken")
                .osType(OsType.Ios)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .param(signUp.toString())

    }

    @Test
    void existedNickname() {
    }
}