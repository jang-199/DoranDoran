package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.service.MemberServiceImpl;
import com.dorandoran.doranserver.service.PolicyTermsCheckImpl;
import com.dorandoran.doranserver.service.SignUpImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Tag(name = "회원가입 관련 API",description = "SignUpController")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SignUpController {

    private final SignUpImpl signUp;
    private final PolicyTermsCheckImpl policyTermsCheck;
    private final MemberServiceImpl memberService;
    private final TokenProvider tokenProvider;

    @Tag(name = "회원가입 관련 API")
    @Operation(summary = "닉네임 중복 체크", description = "요청한 닉네임을 이미 가입된 회원들의 닉네임과 비교하여 중복을 체크합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200",description = "사용가능한 닉네임"),
            @ApiResponse(responseCode = "400",description = "중복된 닉네임")
    })
    @PostMapping("/check-nickname")
    ResponseEntity<?> CheckNickname(@Parameter(description = "중복 체크할 닉네임") @RequestBody NicknameDto nicknameDto) {
        log.info("nicknameDto.getNickname: {}", nicknameDto.getNickname());
        Optional<Member> nickname = signUp.findByNickname(nicknameDto.getNickname());
        if (nickname.isPresent() || nicknameDto.getNickname().isBlank()) {
            log.info("bad req response");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("ok response");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "회원가입 관련 API")
    @Operation(summary = "가입된 회원 체크", description = "요청한 이메일을 조회하여 가입 유무를 체크합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200",description = "가입된 회원"),
            @ApiResponse(responseCode = "400",description = "가입되지 않은 회원")
    })
    @PostMapping("/check/registered")
    ResponseEntity<?> checkRegisteredMember(@RequestBody CheckRegisteredMemberDto memberDto) {
        Optional<Member> byEmail = memberService.findByEmail(memberDto.getEmail());
        if (byEmail.isPresent()) {
            return new ResponseEntity<>(
                    UserInfoDto.builder()
                            .email(byEmail.get().getEmail())
                            .nickName(byEmail.get().getNickname())
                            .build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Tag(name = "회원가입 관련 API")
    @Operation(summary = "회원가입", description = "회원가입 API입니다.")
    @ApiResponses({@ApiResponse(responseCode = "200",
            description = "회원 가입 성공",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserInfoDto.class)
            )),
            @ApiResponse(responseCode = "400",description = "회원가입 실패")
    })
    @PostMapping("/signup")
    ResponseEntity<?> SignUp(@Parameter(description = "가입할 회원 정보") @RequestBody SignUpDto loginDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴

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
}
