package com.dorandoran.doranserver.domain.notification.dto;

import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
import com.dorandoran.doranserver.domain.notification.domain.notificationType.NotificationType;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationDto {
    private static final String notificationTitle = "도란도란";

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationResponse {
        Long notificationId;
        String title;
        String message;
        String notificationTime;
        Boolean isRead;
        NotificationType notificationType;

        @Builder
        public NotificationResponse(NotificationHistory notificationHistory) {
            this.notificationId = notificationHistory.getNotificationHistoryId();
            this.title = notificationTitle;
            this.message = notificationHistory.getMessage();
            this.isRead = notificationHistory.getNotificationReadTime() == null ? Boolean.FALSE : Boolean.TRUE;
            this.notificationTime = notificationHistory.getCreatedTime()
                    .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            this.notificationType = notificationHistory.getNotificationType();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationReadResponse {
        Long notificationId;
        Long postId;
        Long commentId;
        Long replyId;
        NotificationType notificationType;

        @Builder
        public NotificationReadResponse(NotificationHistory notificationHistory, Long postId, Long commentId, Long replyId) {
            this.notificationId = notificationHistory.getNotificationHistoryId();
            this.postId = postId;
            this.commentId = commentId;
            this.replyId = replyId;
            this.notificationType = notificationHistory.getNotificationType();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotificationRemainCountResponse {
        Long remainCount;

        public NotificationRemainCountResponse toEntity(Long remainCount){
            return NotificationRemainCountResponse.builder().
                    remainCount(remainCount).
                    build();
        }

        @Builder
        public NotificationRemainCountResponse(Long remainCount) {
            this.remainCount = remainCount;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotificationReadRequest {
        List<Long> notifcationList;
    }
}
