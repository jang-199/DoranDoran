package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.NotificationHistory;
import com.dorandoran.doranserver.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationHistoryServiceImpl implements NotificationHistoryService{
    private final NotificationHistoryRepository notificationHistoryRepository;
    @Override
    public void saveNotification(NotificationHistory notificationHistory) {
        notificationHistoryRepository.save(notificationHistory);
    }

    @Override
    public void deleteNotification(NotificationHistory notificationHistory) {
        notificationHistoryRepository.delete(notificationHistory);
    }

    @Override
    public void deleteNotificationByMember(Member member) {
        notificationHistoryRepository.deleteAllByMemberId(member);
    }

    @Override
    public List<NotificationHistory> findNotificationByMember(Member member) {
        return notificationHistoryRepository.findByMemberId(member);
    }

    @Override
    public NotificationHistory findNotificationById(Long notificationId) {
        return notificationHistoryRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알람 기록이 없습니다."));
    }
}
