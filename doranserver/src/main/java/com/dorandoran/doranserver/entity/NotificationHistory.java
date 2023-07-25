package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.notificationType.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class NotificationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_HISTORY_ID")
    private Long notificationHistoryId;

    @NotNull
    @Column(name = "MESSAGE")
    private String message;

    @NotNull
    @Column(name = "NOTIFICATION_TIME")
    private LocalDateTime notificationTime;

    @Column(name = "NOTIFICATION_READ_TIME")
    private LocalDateTime notificationReadTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column (name = "OBJECT_ID")
    private Long objectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    public void readNotification(){
        this.notificationReadTime = LocalDateTime.now();
    }
    @Builder
    public NotificationHistory(String message, NotificationType notificationType, Long objectId, Member memberId) {
        this.message = message;
        this.notificationTime = LocalDateTime.now();
        this.notificationReadTime = null;
        this.notificationType = notificationType;
        this.objectId = objectId;
        this.memberId = memberId;
    }
}
