package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
import com.dorandoran.doranserver.domain.member.exception.KakaoResourceServerException;
import com.dorandoran.doranserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public Boolean existedNickname(String nickname) {
        Optional<Member> member = findByNickname(nickname);
        return member.isPresent() ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String getEmailByKakaoResourceServer(String kakaoAccessToken) throws KakaoResourceServerException {
        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            JSONObject jsonObject = new JSONObject(response.getBody());

            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
            return kakao_account.getString("email");


        } catch (HttpClientErrorException e) {
            throw new KakaoResourceServerException(e);
        }
    }
}
