package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.NotificationHistory;
import com.dorandoran.doranserver.entity.notificationType.NotificationType;
import lombok.*;

import java.time.format.DateTimeFormatter;

public class NotificationDto {
    private static final String notificationTitle = "도란도란";

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class notificationResponse {
        Long notificationId;
        String title;
        String message;
        String notificationTime;
        Boolean isRead;
        NotificationType notificationType;

        @Builder
        public notificationResponse(NotificationHistory notificationHistory) {
            this.notificationId = notificationHistory.getNotificationHistoryId();
            this.title = notificationTitle;
            this.message = notificationHistory.getMessage();
            this.isRead = notificationHistory.getNotificationReadTime() == null ? Boolean.FALSE : Boolean.TRUE;
            this.notificationTime = notificationHistory.getNotificationTime()
                    .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            this.notificationType = notificationHistory.getNotificationType();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class notificationReadResponse {
        Long notificationId;
        Long postId;
        Long commentId;
        Long replyId;
        NotificationType notificationType;

        @Builder
        public notificationReadResponse(NotificationHistory notificationHistory, Long postId, Long commentId, Long replyId) {
            this.notificationId = notificationHistory.getNotificationHistoryId();
            this.postId = postId;
            this.commentId = commentId;
            this.replyId = replyId;
            this.notificationType = notificationHistory.getNotificationType();
        }
    }
}
