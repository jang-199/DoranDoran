package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.SignUpDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SignUp {
    //회원가입
    void saveMember(Member member);

    //닉네임 중복 체크(서버에서 특수문자같은 문자들 필터링)
    Optional<Member> findByNickname(String nickname);

    //이용 약관
    Boolean checkPolicyTerms(PolicyTerms policyTerms);
}