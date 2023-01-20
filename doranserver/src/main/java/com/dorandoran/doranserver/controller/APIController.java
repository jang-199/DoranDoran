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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Controller
public class APIController {
    @Value("${background.cnt}")
    Integer backgroundPicCnt;
    private final SignUpImpl signUp;
    private final PolicyTermsCheckImpl policyTermsCheck;
    private final BackGroundPicServiceImpl backGroundPicService;

    @PostMapping("/check-nickname")
    ResponseEntity<?> CheckNickname(@RequestBody NicknameDto nicknameDto) {
        log.info("nicknameDto.getNickname: {}",nicknameDto.getNickname());
        Optional<Member> nickname = signUp.findByNickname(nicknameDto.getNickname());
        if (nickname.isPresent()) {
            log.info("bad req response");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("ok response");
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

    @GetMapping("background/maxcount")
    ResponseEntity<Integer> backgroundPic() {
        return ResponseEntity.ok().body(backgroundPicCnt);
    }


    @GetMapping("background/{picName}")
    ResponseEntity<Resource> eachBackground(@PathVariable Long picName) throws MalformedURLException {

        Optional<BackgroundPic> backgroundPic = backGroundPicService.getBackgroundPic(picName);
        if (backgroundPic.isPresent()) {
//            UrlResource urlResource = new UrlResource("file:" + "/Users/jw1010110/backgroundPic/1.jpg");
            UrlResource urlResource = new UrlResource("file:" + backgroundPic.get().getServerPath());
            log.info("{}",backgroundPic.get().getBackgroundPicId());
            log.info("{}",backgroundPic.get().getImgName());
            log.info("{}",backgroundPic.get().getServerPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backgroundPic.get().getImgName() + "\"")
                    .body(urlResource);
        } else {
            throw new RuntimeException("해당 사진이 존재하지 않습니다.");
        }
    }

    @GetMapping("backgrounds/{picCnt}")
    ResponseEntity<?> backgrounds(@PathVariable Integer picCnt) throws MalformedURLException {
        log.info("{}",picCnt);
        LinkedMultiValueMap<String, UrlResource> multiValueMap = new LinkedMultiValueMap<>();
        UrlResource resource1 = new UrlResource("file:" + "/Users/jw1010110/backgroundPic/1.jpg");
        UrlResource resource2 = new UrlResource("file:" + "/Users/jw1010110/backgroundPic/2.jpg");
        UrlResource resource3 = new UrlResource("file:" + "/Users/jw1010110/backgroundPic/3.jpg");

        multiValueMap.add("1.jpg", resource1);
        multiValueMap.add("2.jpg", resource2);
        multiValueMap.add("3.jpg", resource3);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        return ResponseEntity.ok().headers(httpHeaders).body(multiValueMap);
    }


}