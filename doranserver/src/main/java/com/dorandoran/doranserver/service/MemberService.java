package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;

import java.util.Optional;

public interface MemberService {
    Member findByEmail(String email);

    Boolean findByEmilIsEmpty(String email);

    Member findByRefreshToken(String refreshToken);
    void saveMember(Member member);
    void subtractReportCount(Member member);
}
