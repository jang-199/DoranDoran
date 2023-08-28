package com.dorandoran.doranserver.domain.api.member.repository;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.domain.MemberBlockList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBlockListRepository extends JpaRepository<MemberBlockList, Long> {
    @Query("select m.BlockedMember from MemberBlockList m where m.BlockingMember = :blockingMember")
    List<Member> findByBlockingMember(@Param("blockingMember") Member blockingMember);
}
