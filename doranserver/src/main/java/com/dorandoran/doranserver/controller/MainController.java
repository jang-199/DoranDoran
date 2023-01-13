package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.NicknameDto;
import com.dorandoran.doranserver.dto.SignUpDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.jwt.TokenProvider;
import com.dorandoran.doranserver.service.SignUpImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@Controller
    public class MainController {

        private final TokenProvider tokenProvider;
        private final SignUpImpl signUpImpl;

        @PostMapping("/api/check-nickname")
        ResponseEntity<?> CheckNickname(@RequestBody NicknameDto nicknameDto){
            Optional<Member> nickname = signUpImpl.findByNickname(nicknameDto.getNickname());
            if(nickname.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
                return new ResponseEntity<>(HttpStatus.OK);
        }

        //회원가입(출생년도, 닉네임, udid)
        @PostMapping("/api/signup")
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

                return new ResponseEntity<>(loginDto,HttpStatus.OK);
            } catch (HttpClientErrorException e) {
                log.error("access token err : {}", e.getMessage());
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
}