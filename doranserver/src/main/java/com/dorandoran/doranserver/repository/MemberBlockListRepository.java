package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBlockListRepository extends JpaRepository<MemberBlockList, Long> {
    @Query("select m from MemberBlockList m where m.BlockingMember = :blockingMember")
    List<MemberBlockList> findByBlockingMember(@Param("blockingMember") Member blockingMember);
}
