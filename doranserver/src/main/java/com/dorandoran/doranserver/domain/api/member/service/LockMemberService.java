package com.dorandoran.doranserver.domain.api.member.service;

import com.dorandoran.doranserver.domain.api.member.domain.LockMember;
import com.dorandoran.doranserver.domain.api.member.domain.Member;

import java.util.Optional;

public interface LockMemberService {
    void saveLockMember(LockMember lockMember);
    void deleteLockMember(LockMember lockMember);
    Optional<LockMember> findLockMember(Member member);

    Boolean checkCurrentLocked(LockMember lockMember);
}
