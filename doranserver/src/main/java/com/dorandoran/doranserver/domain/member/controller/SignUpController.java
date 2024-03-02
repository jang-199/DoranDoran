package com.dorandoran.doranserver.domain.member.controller;

import com.dorandoran.doranserver.domain.member.exception.KakaoResourceServerException;
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
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
        if (!nicknameCleaner.isAvailableNickname(nicknameDto.getNickname())) {
            return ResponseEntity.unprocessableEntity().body("사용할 수 없는 닉네임입니다.");
        }

        if (signUpService.existedNickname(nicknameDto.getNickname())) {
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

        String accessToken = tokenProvider.generateAccessToken(member);
        String refreshToken = member.getRefreshToken();
        member.setRefreshToken(refreshToken);
        member.setFirebaseToken(memberDto.getFirebaseToken());
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
    public ResponseEntity<?> changeNickname(@RequestBody AccountDto.ChangeNickname changeNicknameDto,
                                     @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member findMember = memberService.findByEmail(userEmail);

        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        if (!nicknameCleaner.isAvailableNickname(changeNicknameDto.getNickname())) {
            return ResponseEntity.unprocessableEntity().body("사용할 수 없는 닉네임입니다.");
        }

        if (signUpService.existedNickname(changeNicknameDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            memberService.setNickname(findMember, changeNicknameDto.getNickname());
            return ResponseEntity.noContent().build();
        }
    }

    @Trace
    @PostMapping("/member")
    ResponseEntity<?> SignUp(@RequestBody AccountDto.SignUp signUp)  { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴

        NicknameCleaner nicknameCleaner = new NicknameCleaner();
        if (!nicknameCleaner.isAvailableNickname(signUp.getNickname())) {
            return ResponseEntity.unprocessableEntity().body("사용할 수 없는 닉네임입니다.");
        }

        if (memberService.findByEmilIsEmpty(signUp.getEmail())) { //회원 저장 시작

            PolicyTerms policyTerms = PolicyTerms.builder().policy1(true)
                    .policy2(true)
                    .policy3(true)
                    .build();

            policyTermsCheckService.policyTermsSave(policyTerms);


            Member member = Member.builder().dateOfBirth(signUp.getDateOfBirth())
                    .firebaseToken(signUp.getFirebaseToken())
                    .nickname(signUp.getNickname())
                    .policyTermsId(policyTerms)
                    .email(signUp.getEmail())
                    .signUpDate(LocalDateTime.now())
                    .osType(signUp.getOsType().equals(OsType.Aos)?OsType.Aos:OsType.Ios)
                    .refreshToken("Dummy")
                    .checkNotification(signUp.getNotifyStatus()).build();

            String refreshToken = tokenProvider.generateRefreshToken(member); //약 6개월 기간의 refreshToken create

            member.setRefreshToken(refreshToken);

            signUpService.saveMember(member);

            String accessToken = tokenProvider.generateAccessToken(member); //AccessToken generate

            return ResponseEntity.ok().body(
                    AccountDto.SignUpResponse.builder()
                            .email(member.getEmail())
                            .nickname(member.getNickname())
                            .tokenDto(AuthenticationDto.TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                            .build()
            );
        }
        return ResponseEntity.unprocessableEntity().body("이미 존재하는 email 입니다.");
    }
}
