package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberHash;
import com.dorandoran.doranserver.repository.MemberHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberHashServiceImpl implements MemberHashService{

    private final MemberHashRepository memberHashRepository;

    @Override
    public List<MemberHash> findHashByMember(Member member) {
        List<MemberHash> byMember = memberHashRepository.findByMember(member);
        return byMember;
    }

    @Override
    public void saveMemberHash(MemberHash memberHash) {
        memberHashRepository.save(memberHash);
    }

    @Override
    public void deleteMemberHash(MemberHash memberHash) {
        memberHashRepository.delete(memberHash);
    }

    @Override
    public Optional<MemberHash> findMemberHashByEmailAndHashTag(String userEmail, String hashTag) {
        return memberHashRepository.findMemberHashByEmailAndHashTag(userEmail, hashTag);
    }
}
