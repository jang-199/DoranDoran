package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.NotificationHistory;

import java.util.List;

public interface NotificationHistoryService {
    void saveNotification(NotificationHistory notificationHistory);
    void deleteNotification(NotificationHistory notificationHistory);
    void deleteNotificationByMember(Member member);
    List<NotificationHistory> findNotificationByMember(Member member);
    NotificationHistory findNotificationById(Long notificationId);
}
