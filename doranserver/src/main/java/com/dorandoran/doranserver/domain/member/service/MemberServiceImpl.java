package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.repository.MemberRepository;
import com.dorandoran.doranserver.global.util.exception.customexception.member.NotFoundMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(()->new NotFoundMemberException("이메일로 사용자를 찾을 수 없습니다."));
    }

    @Override
    public Boolean findByEmilIsEmpty(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    @Override
    public Member findByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }

    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void subtractReportCount(Member member) {
        member.subtractReportTime();
    }

    @Override
    @Transactional
    public void setNickname(Member member, String nickname) {
        member.setNickname(nickname);
    }
}
