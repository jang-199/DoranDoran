package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.LockMember;
import com.dorandoran.doranserver.entity.Member;

public interface LockMemberService {
    public void saveLockMember(LockMember lockMember);
    public Boolean CheckLocked (Member member);
}
