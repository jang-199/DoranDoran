package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.entity.osType.OsType;
import com.dorandoran.doranserver.service.*;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Timed
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SignUpController {

    private final SignUp signUp;
    private final PolicyTermsCheck policyTermsCheck;
    private final MemberService memberService;
    private final TokenProvider tokenProvider;


    @PostMapping("/check-nickname")
    ResponseEntity<?> CheckNickname(@RequestBody NicknameDto nicknameDto) {

        log.info("nicknameDto.getNickname: {}", nicknameDto.getNickname());
        if (existedNickname(nicknameDto.getNickname())) {
            log.info("해당 닉네임을 사용하는 유저가 존재합니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else {
            log.info("해당 닉네임을 사용하는 유저가 존재하지 않습니다.");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/check/registered")
    ResponseEntity<?> checkRegisteredMember(@RequestBody CheckRegisteredMemberDto memberDto) {
        Optional<Member> byEmail = memberService.findByEmail(memberDto.getEmail());
        if (byEmail.isPresent()) {
            Member member = byEmail.get();
            String accessToken = tokenProvider.generateAccessToken(member, Duration.ofDays(1));
            String refreshToken = tokenProvider.generateRefreshToken(member, Period.ofMonths(6));
            byEmail.get().setRefreshToken(refreshToken);
            byEmail.get().setOsType(memberDto.getOsType().equals(OsType.Aos)?OsType.Aos:OsType.Ios);

            if (byEmail.get().getClosureDate() != null) { //탈퇴 후 삭제 전 재로그인 시
                byEmail.get().setClosureDate(null);
            }
            memberService.saveMember(member);
            return new ResponseEntity<>(
                    UserInfoDto.builder()
                            .email(member.getEmail())
                            .nickName(member.getNickname())
                            .tokenDto(new TokenDto(refreshToken,accessToken))
                            .build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PatchMapping("/change/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody ChangeNicknameDto changeNicknameDto,
                                     @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member findMember = memberService.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException(userEmail));
        if (existedNickname(changeNicknameDto.getNickname())) {
            log.info("해당 닉네임을 사용하는 유저가 존재합니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else {
            log.info("해당 닉네임을 사용하는 유저가 존재하지 않습니다. 변경 가능합니다.");
            findMember.setNickname(changeNicknameDto.getNickname());
            log.info("{} 사용자가 {}에서 {}로 닉네임을 변경하였습니다.",userEmail, findMember.getNickname(), changeNicknameDto.getNickname());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


    @PostMapping("/signup")
    ResponseEntity<?> SignUp(@RequestBody SignUpDto loginDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴

        log.info("fireBaseToken: {}", loginDto.getFirebaseToken());

        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + loginDto.getKakaoAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            log.info("response : {}", response);
            log.info("response.getBody() : {}", response.getBody());
            JSONObject jsonObject = new JSONObject(response.getBody());

            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
            String email = kakao_account.getString("email");
            log.info("email : {}", email);
            if (memberService.findByEmail(email).isEmpty()) { //회원 저장 시작

                PolicyTerms policyTerms = PolicyTerms.builder().policy1(true)
                        .policy2(true)
                        .policy3(true)
                        .build();

                policyTermsCheck.policyTermsSave(policyTerms);


                Member member = Member.builder().dateOfBirth(loginDto.getDateOfBirth())
                        .signUpDate(LocalDateTime.now())
                        .firebaseToken(loginDto.getFirebaseToken())
                        .nickname(loginDto.getNickName())
                        .policyTermsId(policyTerms)
                        .email(email)
                        .osType(loginDto.getOsType().equals(OsType.Aos)?OsType.Aos:OsType.Ios)
                        .refreshToken("Dummy").build();

                String refreshToken = tokenProvider.generateRefreshToken(member, Period.ofMonths(6)); //약 6개월 기간의 refreshToken create

                member.setRefreshToken(refreshToken);
                log.info("refreshToken : {}",refreshToken);

                signUp.saveMember(member);

                String accessToken = tokenProvider.generateAccessToken(member, Duration.ofDays(1)); //AccessToken generate
                log.info("accessToken : {}",accessToken);

                return new ResponseEntity<>(UserInfoDto.builder()
                        .email(member.getEmail())
                        .nickName(member.getNickname())
                        .tokenDto(TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                        .build(), HttpStatus.OK);
            }

        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public Boolean existedNickname(String nickname){
        Optional<Member> member = signUp.findByNickname(nickname);
        return member.isPresent() || nickname.isBlank() ? Boolean.FALSE : Boolean.TRUE;
    }
}
