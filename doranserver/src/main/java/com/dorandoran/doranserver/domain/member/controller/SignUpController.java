package com.dorandoran.doranserver.domain.member.controller;

import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.member.service.PolicyTermsCheck;
import com.dorandoran.doranserver.domain.member.service.SignUp;
import com.dorandoran.doranserver.domain.auth.dto.AuthenticationDto;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
import com.dorandoran.doranserver.domain.member.dto.AccountDto;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.global.config.jwt.TokenProvider;
import com.dorandoran.doranserver.global.util.nicknamecleaner.NicknameCleaner;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Period;
import java.util.Optional;

@Timed
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SignUpController {

    private final SignUp signUpService;
    private final PolicyTermsCheck policyTermsCheckService;
    private final MemberService memberService;
    private final TokenProvider tokenProvider;


    @Trace
    @PostMapping("/nickname")
    ResponseEntity<?> checkNickname(@RequestBody AccountDto.CheckNickname nicknameDto) {
        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        if (nicknameCleaner.isAvailableNickname(nicknameDto.getNickname())) {
            return ResponseEntity.unprocessableEntity().body("사용할 수 없는 닉네임입니다.");
        }

        if (existedNickname(nicknameDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @Trace
    @PostMapping("/registered")
    ResponseEntity<?> checkRegisteredMember(@RequestBody AccountDto.CheckRegisteredMember memberDto) {
        Member member;
        try {
            member = memberService.findByEmail(memberDto.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = tokenProvider.generateAccessToken(member, Duration.ofDays(1));
        String refreshToken = member.getRefreshToken();
        member.setRefreshToken(refreshToken);
        member.setOsType(memberDto.getOsType().equals(OsType.Aos)?OsType.Aos:OsType.Ios);
        if (member.getClosureDate() != null) { //탈퇴 후 삭제 전 재로그인 시
            member.setClosureDate(null);
        }
        memberService.saveMember(member);
        return ResponseEntity.ok()
                .body(AccountDto.CheckRegisteredMemberResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .tokenDto(new AuthenticationDto.TokenResponse(refreshToken,accessToken)).build());
    }

    @Trace
    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody AccountDto.ChangeNickname changeNicknameDto, //todo 비속어, 관리자, 운영자 등등은 막을것
                                     @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member findMember = memberService.findByEmail(userEmail);

        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        if (nicknameCleaner.isAvailableNickname(changeNicknameDto.getNickname())) {
            return ResponseEntity.unprocessableEntity().body("사용할 수 없는 닉네임입니다.");
        }

        if (existedNickname(changeNicknameDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            memberService.setNickname(findMember, changeNicknameDto.getNickname());
            return ResponseEntity.noContent().build();
        }
    }

    @Trace
    @PostMapping("/member")
    ResponseEntity<?> SignUp(@RequestBody AccountDto.SignUp signUp) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴

        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        if (nicknameCleaner.isAvailableNickname(signUp.getNickname())) {
            return ResponseEntity.unprocessableEntity().body("사용할 수 없는 닉네임입니다.");
        }

        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + signUp.getKakaoAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            JSONObject jsonObject = new JSONObject(response.getBody());

            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
            String email = kakao_account.getString("email");
            if (memberService.findByEmilIsEmpty(email)) { //회원 저장 시작

                PolicyTerms policyTerms = PolicyTerms.builder().policy1(true)
                        .policy2(true)
                        .policy3(true)
                        .build();

                policyTermsCheckService.policyTermsSave(policyTerms);


                Member member = Member.builder().dateOfBirth(signUp.getDateOfBirth())
                        .firebaseToken(signUp.getFirebaseToken())
                        .nickname(signUp.getNickname())
                        .policyTermsId(policyTerms)
                        .email(email)
                        .osType(signUp.getOsType().equals(OsType.Aos)?OsType.Aos:OsType.Ios)
                        .refreshToken("Dummy").build();

                String refreshToken = tokenProvider.generateRefreshToken(member, Period.ofMonths(6)); //약 6개월 기간의 refreshToken create

                member.setRefreshToken(refreshToken);

                signUpService.saveMember(member);

                String accessToken = tokenProvider.generateAccessToken(member, Duration.ofDays(1)); //AccessToken generate

                return ResponseEntity.ok().body(
                        AccountDto.SignUpResponse.builder()
                        .email(member.getEmail())
                        .nickname(member.getNickname())
                        .tokenDto(AuthenticationDto.TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                        .build()
                );
            }

        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    public Boolean existedNickname(String nickname){
        Optional<Member> member = signUpService.findByNickname(nickname);
        return member.isPresent()  ? Boolean.TRUE : Boolean.FALSE;
    }
}
