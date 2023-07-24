package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    void deleteAllByMemberId(Member member);

    List<NotificationHistory> findByMemberId(Member member);
}
