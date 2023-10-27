package com.dorandoran.doranserver.domain.notification.repository;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    void deleteAllByMemberId(Member member);

    @Query("select not from NotificationHistory not where not.memberId = :member " +
            "order by not.notificationHistoryId desc ")
    List<NotificationHistory> findFirstNotByMemberId(@Param("member") Member member,
                                                     PageRequest pageRequest);

    @Query("select not from NotificationHistory not where not.memberId = :member and not.notificationHistoryId < :notCnt " +
            "order by not.notificationHistoryId desc ")
    List<NotificationHistory> findNotByMemberId(@Param("member") Member member,
                                                @Param("notCnt") Long notCnt,
                                                PageRequest pageRequest);

    List<NotificationHistory> findAllByMemberId(Member member);

    @Query("select count(not) from NotificationHistory not where not.memberId = :member and not.notificationReadTime = null")
    Long findRemainNotificationMemberHistoryCount(@Param("member") Member member);
}
