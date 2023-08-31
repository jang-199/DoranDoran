package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.Member;

import java.util.List;

public interface MemberBlockListService {
    void addBlockList(Member blockingMember, Member blockedMember);

    List<Member> findMemberBlockListByBlockingMember(Member blockingMember);
}
