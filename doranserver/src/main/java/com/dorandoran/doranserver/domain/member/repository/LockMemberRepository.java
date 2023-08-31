package com.dorandoran.doranserver.domain.member.repository;

import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LockMemberRepository extends JpaRepository<LockMember, Long> {
    Optional<LockMember> findLockMemberByMemberId(Member member);
}
