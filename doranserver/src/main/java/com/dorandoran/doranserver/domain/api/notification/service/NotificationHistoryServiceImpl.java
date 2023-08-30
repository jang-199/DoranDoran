package com.dorandoran.doranserver.domain.api.notification.service;

import com.dorandoran.doranserver.domain.api.comment.service.CommentService;
import com.dorandoran.doranserver.domain.api.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.api.notification.dto.NotificationDto;
import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.notification.domain.NotificationHistory;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class NotificationHistoryServiceImpl implements NotificationHistoryService{
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final CommentService commentService;
    private final ReplyService replyService;
    @Override
    public void saveNotification(NotificationHistory notificationHistory) {
        notificationHistoryRepository.save(notificationHistory);
    }

    @Override
    public void deleteNotification(NotificationHistory notificationHistory) {
        notificationHistoryRepository.delete(notificationHistory);
    }

    @Override
    @Transactional
    public void deleteNotificationByMember(Member member) {
        notificationHistoryRepository.deleteAllByMemberId(member);
    }

    @Override
    public List<NotificationHistory> findNotification(Member member, Long notCnt) {
        PageRequest of = PageRequest.of(0, 20);
        return notificationHistoryRepository.findNotByMemberId(member, notCnt, of);
    }

    @Override
    public List<NotificationHistory> findFirstNotification(Member member) {
        PageRequest of = PageRequest.of(0, 20);
        return notificationHistoryRepository.findFirstNotByMemberId(member, of);
    }

    @Override
    public NotificationHistory findNotificationById(Long notificationId) {
        return notificationHistoryRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알람 기록이 없습니다."));
    }

    @Override
    @Transactional
    public NotificationDto.notificationReadResponse readNotification(NotificationHistory notificationHistory) {
        notificationHistory.readNotification();
        return setNotificationResponseDto(notificationHistory);
    }

    private NotificationDto.notificationReadResponse makePostNotification(NotificationHistory notificationHistory) {
        return NotificationDto.notificationReadResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(notificationHistory.getObjectId())
                .commentId(null)
                .replyId(null)
                .build();
    }

    private NotificationDto.notificationReadResponse makeCommentNotification(Long notificationObjectId, NotificationHistory notificationHistory) {
        Comment comment = commentService.findCommentByCommentId(notificationObjectId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));
        return NotificationDto.notificationReadResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(comment.getPostId().getPostId())
                .commentId(notificationObjectId)
                .replyId(null)
                .build();
    }

    private NotificationDto.notificationReadResponse makeReplyNotification(NotificationHistory notificationHistory) {
        Reply reply = replyService.findReplyByReplyId(notificationHistory.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("해당 대댓글이 존재하지 않습니다."));
        return NotificationDto.notificationReadResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(reply.getCommentId().getPostId().getPostId())
                .commentId(reply.getCommentId().getCommentId())
                .replyId(notificationHistory.getObjectId())
                .build();
    }

    private NotificationDto.notificationReadResponse setNotificationResponseDto(NotificationHistory notificationHistory) {
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
            default -> throw new IllegalArgumentException("해당 타입의 알림은 존재하지않습니다.");
        }
    }
}
