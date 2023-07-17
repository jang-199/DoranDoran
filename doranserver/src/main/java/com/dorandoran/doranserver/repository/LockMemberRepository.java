package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LockMemberRepository extends JpaRepository<LockMember, Long> {
    Optional<LockMember> findLockMemberByMemberId(Member member);
}
