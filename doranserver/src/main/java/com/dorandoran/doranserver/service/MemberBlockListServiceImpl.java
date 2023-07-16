package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.repository.MemberBlockListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
