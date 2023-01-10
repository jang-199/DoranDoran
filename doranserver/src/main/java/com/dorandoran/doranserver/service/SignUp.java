package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;

public interface SignUp {
    //회원가입
    void saveMember(Member member);

    //닉네임 중복 체크(서버에서 특수문자같은 문자들 필터링)
    Boolean checkNickname(Member member);

    //이용 약관
    Boolean checkPolicyTerms(PolicyTerms policyTerms);
}