package com.dorandoran.doranserver.domain.api.notification.domain;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.notification.domain.notificationType.NotificationType;
import com.dorandoran.doranserver.domain.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "NOTIFICATION_TIME"))
public class NotificationHistory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_HISTORY_ID")
    private Long notificationHistoryId;

    @NotNull
    @Column(name = "MESSAGE")
    private String message;

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
        this.notificationReadTime = null;
        this.notificationType = notificationType;
        this.objectId = objectId;
        this.memberId = memberId;
    }
}
