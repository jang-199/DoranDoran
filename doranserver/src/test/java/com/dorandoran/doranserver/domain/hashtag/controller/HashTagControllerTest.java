package com.dorandoran.doranserver.domain.hashtag.controller;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.hashtag.dto.HashTagDto;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.MemberHashService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class HashTagControllerTest {
    private final String refreshToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private HashTagService hashTagService;
    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberHashService memberHashService;

    @Test
    void searchHashTag() throws Exception{
        //given
        List<String> hashTagNameList = List.of("첫 번째", "두 번째");
        List<HashTag> hashTagList = setHashTagList(List.of("첫 번째", "두 번째", "세 번째"));
        BDDMockito.given(memberHashService.findHashByEmail(BDDMockito.any())).willReturn(hashTagNameList);
        BDDMockito.given(hashTagService.findTop5BySearchHashTag(BDDMockito.any())).willReturn(hashTagList);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/hashTag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", refreshToken)
                        .param("hashTag", "첫")
        );

        //given
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].hashTagName").value("첫 번째"));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[1].hashTagName").value("두 번째"));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[2].hashTagName").value("세 번째"));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[2].hashTagCheck").value(false));
    }

    @Test
    void popularHashTag() throws Exception{
        //given
        List<HashTag> hashTagList = setHashTagList(List.of("테스트1", "테스트2"));
        BDDMockito.given(hashTagService.findPopularHashTagTop5()).willReturn(hashTagList);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/hashTag/popular")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].hashTagName").value("테스트1"));
    }

    @Test
    void memberHashTag() throws Exception{
        //given
        List<String> hashTagList = List.of("테스트1", "테스트2", "테스트3");
        BDDMockito.given(memberHashService.findHashByEmail(Mockito.any())).willReturn(hashTagList);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/hashTag/member")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$['hashTagList'].[0]").value("테스트1"));
    }

    @Test
    void saveHashTag() throws Exception{
        //given
        Member member = setMember();
        HashTag hashTag = setHashTag("테스트");
        List<String> notContainLikeHashTag = List.of("테스트1", "테스트2", "테스트3");
        List<String> containLikeHashTag = List.of("테스트", "테스트1", "테스트2", "테스트3");
        HashTagDto.CreateHashTag hashTagDto = HashTagDto.CreateHashTag.builder().hashTag("테스트").build();
        String content = new ObjectMapper().writeValueAsString(hashTagDto);

        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(hashTagService.findByHashTagName(BDDMockito.any())).willReturn(hashTag);
        BDDMockito.given(memberHashService.findHashByEmail(BDDMockito.any())).willReturn(containLikeHashTag).willReturn(notContainLikeHashTag);
        BDDMockito.doNothing().when(memberHashService).saveMemberHash(BDDMockito.any());

        //when
        ResultActions saveResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/hashTag/member")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions notSaveResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/hashTag/member")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        saveResultActions.andExpect(MockMvcResultMatchers.status().isConflict());
        notSaveResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void deleteHashTag() throws Exception{
    }

    private static Member setMember() {
        return Member.builder()
                .email("9643us@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000, 12, 12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();
    }

    private static HashTag setHashTag(String hashtagName) {
        return HashTag.builder().hashTagName(hashtagName).hashTagCount(1L).build();
    }

    private static List<HashTag> setHashTagList(List<String> hashtagNameList) {
        ArrayList<HashTag> hashtagList = new ArrayList<>();

        for (String hashtagName : hashtagNameList) {
            HashTag hashTag = HashTag.builder().hashTagName(hashtagName).hashTagCount(1L).build();
            hashtagList.add(hashTag);
        }

        return hashtagList;
    }
}