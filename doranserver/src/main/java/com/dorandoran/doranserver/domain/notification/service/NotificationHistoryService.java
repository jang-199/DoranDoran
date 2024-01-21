package com.dorandoran.doranserver.domain.notification.service;

import com.dorandoran.doranserver.domain.notification.dto.NotificationDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;

import java.util.List;

public interface NotificationHistoryService {
    void saveNotification(NotificationHistory notificationHistory);
    void deleteNotification(NotificationHistory notificationHistory);
    void deleteNotificationByMember(Member member);

    List<NotificationHistory> findNotification(Member member, Long notCnt);

    List<NotificationHistory> findFirstNotification(Member member);

    NotificationHistory findNotificationById(Long notificationId);
    NotificationDto.NotificationReadResponse readNotification(NotificationHistory notificationHistory);

    Long findRemainMemberNotificationHistoryCount(Member member);

    void patchNotificationListReadTime(List<NotificationHistory> notifcationHistoryList);

    List<NotificationHistory> findNotificationHistoryList(List<Long> requestNotifcationList);
}
