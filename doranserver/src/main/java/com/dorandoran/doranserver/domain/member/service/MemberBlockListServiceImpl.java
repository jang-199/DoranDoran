package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.MemberBlockList;
import com.dorandoran.doranserver.domain.member.repository.MemberBlockListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberBlockListServiceImpl implements MemberBlockListService{

    private final MemberBlockListRepository memberBlockListRepository;

    @Override
    public void addBlockList(Member blockingMember, Member blockedMember) {
        MemberBlockList memberBlockList = MemberBlockList.builder().BlockingMember(blockingMember)
                .BlockedMember(blockedMember)
                .build();
        memberBlockListRepository.save(memberBlockList);
    }

    @Override
    public List<Member> findMemberBlockListByBlockingMember(Member blockingMember) {
        return memberBlockListRepository.findByBlockingMember(blockingMember);
    }
}
