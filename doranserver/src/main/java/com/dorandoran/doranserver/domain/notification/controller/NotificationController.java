package com.dorandoran.doranserver.domain.notification.controller;

import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.notification.dto.NotificationDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.service.NotificationHistoryService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Timed
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
public class NotificationController {
    private final MemberService memberService;
    private final NotificationHistoryService notificationHistoryService;

    @Trace
    @GetMapping("/notification/{notCnt}")
    ResponseEntity<List<NotificationDto.notificationResponse>> retrieveNotification(@PathVariable Long notCnt,
                                                                                    @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        List<NotificationHistory> notificationList =
                notCnt == 0
                ? notificationHistoryService.findFirstNotification(member)
                : notificationHistoryService.findNotification(member, notCnt);

        List<NotificationDto.notificationResponse> notificationResponse = notificationList.stream()
                .map((notificationHistory) -> NotificationDto.notificationResponse.builder()
                        .notificationHistory(notificationHistory)
                        .build())
                .toList();

        return ResponseEntity.ok().body(notificationResponse);
    }

    @Trace
    @GetMapping("/notification/{notificationId}/detail")
    ResponseEntity<NotificationDto.notificationReadResponse> retrieveNotificationDetail(@PathVariable Long notificationId){
        NotificationHistory notification = notificationHistoryService.findNotificationById(notificationId);
        NotificationDto.notificationReadResponse notificationReadResponse = notificationHistoryService.readNotification(notification);
        return ResponseEntity.ok().body(notificationReadResponse);
    }

    @Trace
    @DeleteMapping("/notification/{notificationId}")
    ResponseEntity<String> deleteNotification(@PathVariable Long notificationId,
                                              @AuthenticationPrincipal UserDetails userDetails){
        if (notificationId == 0){
            Member member = memberService.findByEmail(userDetails.getUsername());
            notificationHistoryService.deleteNotificationByMember(member);
        }else {
            NotificationHistory notification = notificationHistoryService.findNotificationById(notificationId);
            notificationHistoryService.deleteNotification(notification);
        }
        return ResponseEntity.noContent().build();
    }

    @Trace
    @GetMapping("/notification/count")
    public ResponseEntity<?> retrieveRemainNotificationCount(@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Long remainHistoryCount = notificationHistoryService.findRemainMemberNotificationHistoryCount(member);
        NotificationDto.notificationRemainCountResponse countResponseDto =
                new NotificationDto.notificationRemainCountResponse().toEntity(remainHistoryCount);

        return ResponseEntity.ok().body(countResponseDto);
    }
}
