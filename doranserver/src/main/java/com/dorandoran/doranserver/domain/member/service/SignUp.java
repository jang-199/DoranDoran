package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
import com.dorandoran.doranserver.domain.member.exception.KakaoResourceServerException;

import java.util.Optional;

public interface SignUp {
    //회원가입
    void saveMember(Member member);

    //닉네임 중복 체크(서버에서 특수문자같은 문자들 필터링)
    Optional<Member> findByNickname(String nickname);

    //이용 약관
    Boolean checkPolicyTerms(PolicyTerms policyTerms);

    Boolean existedNickname(String nickname);
    String getEmailByKakaoResourceServer(String kakaoAccessToken) throws KakaoResourceServerException;
}