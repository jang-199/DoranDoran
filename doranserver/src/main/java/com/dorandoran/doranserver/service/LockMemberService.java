package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.entity.Member;

import java.util.Optional;

public interface LockMemberService {
    void saveLockMember(LockMember lockMember);
    void deleteLockMember(LockMember lockMember);
    Optional<LockMember> findLockMember(Member member);

    Boolean checkCurrentLocked(LockMember lockMember);
}
