package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;

public interface MemberBlockListService {
    void addBlockList(Member blockingMember, Member blockedMember);
}
