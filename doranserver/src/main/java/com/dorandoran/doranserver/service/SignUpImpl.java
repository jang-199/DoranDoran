package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.SignUpDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SignUpImpl implements SignUp{
    private final MemberRepository memberRepository;

    @Override
    public void saveMember(Member member) {

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
