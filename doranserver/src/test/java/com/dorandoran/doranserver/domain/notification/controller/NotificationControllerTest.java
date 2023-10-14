package com.dorandoran.doranserver.domain.notification.controller;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
import com.dorandoran.doranserver.domain.notification.domain.notificationType.NotificationType;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.domain.notification.dto.NotificationDto;
import com.dorandoran.doranserver.domain.notification.repository.NotificationHistoryRepository;
import com.dorandoran.doranserver.domain.notification.service.NotificationHistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String REFRESH_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @MockBean
    private NotificationHistoryService notificationHistoryService;
    @Test
    void retrieveNotification() throws Exception {
        //given
        Member member = setMember();
        List<NotificationHistory> notificationHistoryList = setNotificationHistoryList(member);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(notificationHistoryService.findFirstNotification(BDDMockito.any())).willReturn(notificationHistoryList);
        BDDMockito.given(notificationHistoryService.findNotification(BDDMockito.any(), BDDMockito.any())).willReturn(notificationHistoryList);

        //when
        ResultActions getFirstHistoryResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/notification/0")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        ResultActions getNotFirstHistoryResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/notification/1")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        //then
        getFirstHistoryResultActions.andExpect(MockMvcResultMatchers.status().isOk());
        getNotFirstHistoryResultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value( "테스트"));
        getNotFirstHistoryResultActions.andExpect(MockMvcResultMatchers.status().isOk());
        getNotFirstHistoryResultActions.andExpect(MockMvcResultMatchers.jsonPath("*").isArray());
    }

    @Test
    void retrieveNotificationDetail() throws Exception{
        //given
        Member member = setMember();
        NotificationHistory notificationHistory = setNotificationHistory(member, NotificationType.PostLike);
        NotificationDto.notificationReadResponse notificationReadResponse = setResponseDto(notificationHistory);

        BDDMockito.given(notificationHistoryService.findNotificationById(BDDMockito.any())).willReturn(notificationHistory);
        BDDMockito.given(notificationHistoryService.readNotification(BDDMockito.any())).willReturn(notificationReadResponse);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/notification/1/detail")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$['notificationType']").value("PostLike"));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$['postId']").value(1L));
    }

    @Test
    void deleteNotification() throws Exception{
        //given
        Member member = setMember();
        NotificationHistory notificationHistory = setNotificationHistory(member, NotificationType.PostLike);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.doNothing().when(notificationHistoryService).deleteNotificationByMember(member);
        BDDMockito.given(notificationHistoryService.findNotificationById(BDDMockito.any())).willReturn(notificationHistory);
        BDDMockito.doNothing().when(notificationHistoryService).deleteNotification(BDDMockito.any());

        //when
        ResultActions deleteEntireMemberHistoryResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/notification/{notificationId}", 0)
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        ResultActions deleteHistoryByIdResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/notification/{notificationId}", 1L)
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        //then
        deleteEntireMemberHistoryResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        deleteHistoryByIdResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
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

    private static NotificationHistory setNotificationHistory(Member member, NotificationType notificationType) {
        NotificationHistory notificationHistory = NotificationHistory.builder()
                .memberId(member)
                .notificationType(notificationType)
                .objectId(1L)
                .message("테스트")
                .build();

        notificationHistory.setCreatedTime(LocalDateTime.now());

        return notificationHistory;
    }

    private static List<NotificationHistory> setNotificationHistoryList(Member member) {
        NotificationHistory postLike = setNotificationHistory(member, NotificationType.PostLike);
        NotificationHistory commentList = setNotificationHistory(member, NotificationType.CommentLike);
        NotificationHistory comment = setNotificationHistory(member, NotificationType.Comment);
        NotificationHistory reply = setNotificationHistory(member, NotificationType.Reply);

        return List.of(postLike, commentList, comment, reply);
    }

    private static NotificationDto.notificationReadResponse setResponseDto(NotificationHistory notificationHistory) {
        return NotificationDto.notificationReadResponse
                .builder()
                .notificationHistory(notificationHistory)
                .postId(1L)
                .commentId(null)
                .replyId(null)
                .build();
    }
}