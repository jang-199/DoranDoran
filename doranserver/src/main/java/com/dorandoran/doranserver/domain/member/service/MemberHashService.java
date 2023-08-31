package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.MemberHash;

import java.util.List;
import java.util.Optional;

public interface MemberHashService {
    void saveMemberHash(MemberHash memberHash);
    void deleteMemberHash(MemberHash memberHash);
    Optional<MemberHash> findMemberHashByEmailAndHashTag(String userEmail, String hashTag);
    List<MemberHash> findHashByMember(Member member);
    List<String> findHashByEmail(String userEmail);
}
