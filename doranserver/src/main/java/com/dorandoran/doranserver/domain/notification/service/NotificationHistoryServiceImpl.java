package com.dorandoran.doranserver.domain.notification.service;

import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.notification.dto.NotificationDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.notification.repository.NotificationHistoryRepository;
import com.dorandoran.doranserver.global.util.exception.customexception.notification.NotFoundNotificationHistoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
                .orElseThrow(() -> new NotFoundNotificationHistoryException("해당 알람 기록이 없습니다."));
    }

    @Override
    @Transactional
    public NotificationDto.NotificationReadResponse readNotification(NotificationHistory notificationHistory) {
        notificationHistory.readNotification();
        return setNotificationResponseDto(notificationHistory);
    }

    @Override
    public Long findRemainMemberNotificationHistoryCount(Member member) {
        return notificationHistoryRepository.findRemainNotificationMemberHistoryCount(member);
    }

    @Override
    public List<NotificationHistory> findNotificationHistoryList(List<Long> requestNotifcationList) {
        return notificationHistoryRepository.findListByNotificationHistoryId(requestNotifcationList);
    }

    @Override
    @Transactional
    public void patchNotificationListReadTime(List<NotificationHistory> notifcationHistoryList) {
        for (NotificationHistory notificationHistory : notifcationHistoryList) {
            notificationHistory.setNotificationReadTime(LocalDateTime.now());
        }
    }



    private NotificationDto.NotificationReadResponse makePostNotification(NotificationHistory notificationHistory) {
        return NotificationDto.NotificationReadResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(notificationHistory.getObjectId())
                .commentId(null)
                .replyId(null)
                .build();
    }

    private NotificationDto.NotificationReadResponse makeCommentNotification(Long notificationObjectId, NotificationHistory notificationHistory) {
        Comment comment = commentService.findCommentByCommentId(notificationObjectId);
        return NotificationDto.NotificationReadResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(comment.getPostId().getPostId())
                .commentId(notificationObjectId)
                .replyId(null)
                .build();
    }

    private NotificationDto.NotificationReadResponse makeReplyNotification(NotificationHistory notificationHistory) {
        Reply reply = replyService.findReplyByReplyId(notificationHistory.getObjectId());
        return NotificationDto.NotificationReadResponse.builder()
                .notificationHistory(notificationHistory)
                .postId(reply.getCommentId().getPostId().getPostId())
                .commentId(reply.getCommentId().getCommentId())
                .replyId(notificationHistory.getObjectId())
                .build();
    }

    private NotificationDto.NotificationReadResponse setNotificationResponseDto(NotificationHistory notificationHistory) {
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
