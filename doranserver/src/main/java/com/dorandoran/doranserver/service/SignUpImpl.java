package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignUpImpl implements SignUp{

    private final MemberRepository memberRepository;

    /***
     * Description parameter로 전달된 Member 객체를 DB에 저장
     * @param member
     */
    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Optional<Member> findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    @Override
    public Boolean checkPolicyTerms(PolicyTerms policyTerms) {
        return null;
    }
}
