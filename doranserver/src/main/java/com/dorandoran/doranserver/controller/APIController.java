package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.NicknameDto;
import com.dorandoran.doranserver.dto.SignUpDto;
import com.dorandoran.doranserver.entity.BackgroundPic;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.service.BackGroundPicServiceImpl;
import com.dorandoran.doranserver.service.PolicyTermsCheckImpl;
import com.dorandoran.doranserver.service.SignUpImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Controller
public class APIController {
    private final SignUpImpl signUp;
    private final PolicyTermsCheckImpl policyTermsCheck;
    private final BackGroundPicServiceImpl backGroundPicService;

    @PostMapping("/check-nickname")
    ResponseEntity<?> CheckNickname(@RequestBody NicknameDto nicknameDto) {
        Optional<Member> nickname = signUp.findByNickname(nicknameDto.getNickname());
        if (nickname.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원가입(출생년도, 닉네임, udid)
    @PostMapping("/signup")
    ResponseEntity<?> SignUp(@RequestBody SignUpDto loginDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴

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
                    .email(email).build();

            signUp.saveMember(member);

            return new ResponseEntity<>(member.getEmail(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("background/{picCnt}")
    ResponseEntity<?> backgroundPic(@PathVariable Integer picCnt) {
        log.info("{}", picCnt);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("background/{imgName}")
    ResponseEntity<Resource> eachBackground(@PathVariable String imgName) throws MalformedURLException {
        Optional<BackgroundPic> backgroundPic = backGroundPicService.getBackgroundPic(imgName);
        if (backgroundPic.isPresent()){

            UrlResource urlResource = new UrlResource("file:" + backgroundPic.get().getServerPath());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment; filename=\"" + backgroundPic.get().getImgName() + "\"");
            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .body(urlResource);
            
        }
        else {
            throw new RuntimeException("해당 사진이 존재하지 않습니다.");
        }

    }
}