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
    @GetMapping("/notification/{notificationId}")
    public ResponseEntity<List<NotificationDto.notificationResponse>> retrieveNotification(@PathVariable("notificationId") Long notificationId,
                                                                                           @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        List<NotificationHistory> notificationList = notificationId==0
                ? notificationHistoryService.findFirstNotification(member)
                : notificationHistoryService.findNotification(member, notificationId);

        List<NotificationDto.notificationResponse> notificationResponseList = makeNotificationResponseList(notificationList);

        return ResponseEntity.ok().body(notificationResponseList);
    }

    @DeleteMapping("/notification")
    public ResponseEntity<String> deleteNotification(@RequestBody NotificationDto.notification notificationRequest){
        NotificationHistory notification = notificationHistoryService.
                findNotificationById(notificationRequest.getNotificationId());
        notificationHistoryService.deleteNotification(notification);
        return ResponseEntity.ok().body("해당 알람이 삭제되었습니다.");
    }
    @DeleteMapping("/notification/member")
    public ResponseEntity<String> deleteNotificationByMember(@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        notificationHistoryService.deleteNotificationByMember(member);
        return ResponseEntity.ok().body("해당 사용자의 모든 알람 삭제가 완료되었습니다.");
    }

    private static NotificationDto.notificationResponse makePostNotification(NotificationHistory notificationHistory) {
        return NotificationDto.notificationResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(notificationHistory.getObjectId())
                .commentId(null)
                .replyId(null)
                .build();
    }

    private NotificationDto.notificationResponse makeCommentNotification(Long notificationObjectId, NotificationHistory notificationHistory) {
        Comment comment = commentService.findCommentByCommentId(notificationObjectId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));
        return NotificationDto.notificationResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(comment.getPostId().getPostId())
                .commentId(notificationObjectId)
                .replyId(null)
                .build();
    }

    private NotificationDto.notificationResponse makeReplyNotification(NotificationHistory notificationHistory) {
        Reply reply = replyService.findReplyByReplyId(notificationHistory.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("해당 대댓글이 존재하지 않습니다."));
        return NotificationDto.notificationResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(reply.getCommentId().getPostId().getPostId())
                .commentId(reply.getCommentId().getCommentId())
                .replyId(notificationHistory.getObjectId())
                .build();
    }

    private List<NotificationDto.notificationResponse> makeNotificationResponseList(List<NotificationHistory> notificationList) {
        return notificationList.stream()
                .map(notificationHistory -> {
                    switch (notificationHistory.getNotificationType()) {
                        case PostLike -> {
                            return makePostNotification(notificationHistory);
                        }
                        case CommentLike, Comment -> {
                            return makeCommentNotification(notificationHistory.getObjectId(), notificationHistory);
                        }
                        case Reply -> {
                            return makeReplyNotification(notificationHistory);
                        }
                        default -> {
                            return null;
                        }
                    }
                }).collect(Collectors.toList());
    }
}
