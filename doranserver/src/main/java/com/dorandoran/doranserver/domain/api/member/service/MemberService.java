package com.dorandoran.doranserver.domain.api.member.service;

import com.dorandoran.doranserver.domain.api.member.domain.Member;

public interface MemberService {
    Member findByEmail(String email);

    Boolean findByEmilIsEmpty(String email);

    Member findByRefreshToken(String refreshToken);
    void saveMember(Member member);
    void subtractReportCount(Member member);
}
