package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;

import java.util.List;

public interface MemberBlockListService {
    void addBlockList(Member blockingMember, Member blockedMember);

    List<MemberBlockList> findMemberBlockListByBlockingMember(Member blockingMember);
}
