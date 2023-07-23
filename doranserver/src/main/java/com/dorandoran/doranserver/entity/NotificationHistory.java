package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.notificationType.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "POST_ID")
    private Post postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;
}
