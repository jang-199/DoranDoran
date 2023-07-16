package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(()->new RuntimeException("이메일로 사용자를 찾을 수 없습니다."));
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
}
