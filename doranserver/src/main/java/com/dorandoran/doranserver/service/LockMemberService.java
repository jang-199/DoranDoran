package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.entity.Member;

import java.util.Optional;

public interface LockMemberService {
    public void saveLockMember(LockMember lockMember);
    public Optional<LockMember> findLockMember(Member member);
    public void deleteLockMember(LockMember lockMember);
    public Boolean checkCurrentLocked(LockMember lockMember);
}
