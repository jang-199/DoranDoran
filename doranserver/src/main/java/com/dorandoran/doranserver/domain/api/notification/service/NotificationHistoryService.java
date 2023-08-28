package com.dorandoran.doranserver.domain.api.notification.service;

import com.dorandoran.doranserver.domain.api.notification.dto.NotificationDto;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.notification.domain.NotificationHistory;

import java.util.List;

public interface NotificationHistoryService {
    void saveNotification(NotificationHistory notificationHistory);
    void deleteNotification(NotificationHistory notificationHistory);
    void deleteNotificationByMember(Member member);

    List<NotificationHistory> findNotification(Member member, Long notCnt);

    List<NotificationHistory> findFirstNotification(Member member);

    NotificationHistory findNotificationById(Long notificationId);
    NotificationDto.notificationReadResponse readNotification(NotificationHistory notificationHistory);
}
