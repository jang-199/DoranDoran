package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;

import java.util.Optional;

public interface MemberService {
    public Optional<Member> findByEmail(String email);

    Member findByRefreshToken(String refreshToken);
    public void saveMember(Member member);
}
