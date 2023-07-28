package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.NotificationHistory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.awt.print.Pageable;
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
}
