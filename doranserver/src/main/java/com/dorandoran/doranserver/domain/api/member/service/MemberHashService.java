package com.dorandoran.doranserver.domain.api.member.service;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.domain.MemberHash;

import java.util.List;
import java.util.Optional;

public interface MemberHashService {
    void saveMemberHash(MemberHash memberHash);
    void deleteMemberHash(MemberHash memberHash);
    Optional<MemberHash> findMemberHashByEmailAndHashTag(String userEmail, String hashTag);
    List<MemberHash> findHashByMember(Member member);
    List<String> findHashByEmail(String userEmail);
}
