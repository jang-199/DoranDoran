package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.NotificationDto;
import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.NotificationHistory;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.service.*;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Timed
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
public class NotificationController {
    private final MemberService memberService;
    private final NotificationHistoryService notificationHistoryService;
    private final CommentService commentService;
    private final ReplyService replyService;
    @GetMapping("/notification/{notCnt}")
    ResponseEntity<List<NotificationDto.notificationResponse>> retrieveNotification(@PathVariable Long notCnt,
                                                                                    @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        log.info("{}", member);
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

    @GetMapping("/notification/{notificationId}/read")
    ResponseEntity<NotificationDto.notificationReadResponse> retrieveNotificationDetail(@PathVariable Long notificationId){
        NotificationHistory notification = notificationHistoryService.findNotificationById(notificationId);
        NotificationDto.notificationReadResponse notificationReadResponse = notificationHistoryService.readNotification(notification);
        return ResponseEntity.ok().body(notificationReadResponse);
    }

    @DeleteMapping("/notification")
    ResponseEntity<String> deleteNotification(@RequestBody NotificationDto.notification notificationRequest){
        NotificationHistory notification = notificationHistoryService.
                findNotificationById(notificationRequest.getNotificationId());
        notificationHistoryService.deleteNotification(notification);
        return ResponseEntity.ok().body("해당 알람이 삭제되었습니다.");
    }
    @DeleteMapping("/notification/member")
    ResponseEntity<String> deleteNotificationByMember(@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        notificationHistoryService.deleteNotificationByMember(member);
        return ResponseEntity.ok().body("해당 사용자의 모든 알람 삭제가 완료되었습니다.");
    }
}
