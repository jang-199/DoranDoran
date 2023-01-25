package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
@RestController
@Controller
public class APIController {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${background.cnt}")
    Integer backgroundPicCnt;
    private final SignUpImpl signUp;
    private final PolicyTermsCheckImpl policyTermsCheck;
    private final BackGroundPicServiceImpl backGroundPicService;
    private final MemberService memberService;
    private final UserUploadPicServiceImpl userUploadPicService;
    private final PostLikeServiceImpl postLikeService;
    private final HashTagServiceImpl hashTagService;
    private final PostServiceImpl postService;

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
            if (memberService.findByEmail(email).isEmpty()) {
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
            }

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

    @PostMapping("/post")
    ResponseEntity<?> createPost(@RequestParam MultipartFile file,
                                 @RequestBody PostDto postDto,
                                 @RequestBody BackgroundPicDto backgroundPicDto,
                                 @RequestBody MemberDto memberDto,
                                 @RequestBody HashTagDto hashTagDto) throws IOException {

        Optional<Member> memberEmail = memberService.findByEmail(memberDto.getEmail());

        log.info("{}의 글 생성",memberEmail.get().getNickname());
        Post post = Post.builder()
                .content(postDto.getContent())
                .forMe(postDto.getForMe())
                .postTime(new Date())
                .location(postDto.getLocation())
                .memberId(memberEmail.get())
                .build();
        if(!file.isEmpty()) {
            String userUploadImgName = UUID.randomUUID().toString();
            post.setSwitchPic(ImgType.UserUpload);
            post.setImgName(userUploadImgName);
            UserUploadPic userUploadPic = UserUploadPic
                    .builder()
                    .imgName(userUploadImgName)
                    .serverPath(userUploadPicServerPath)
                    .build();
            userUploadPicService.saveUserUploadPic(userUploadPic);
            file.transferTo(new File(userUploadPicServerPath));
        }
        else {
            post.setSwitchPic(ImgType.DefaultBackground);
            post.setImgName(backgroundPicDto.getBackgroundImgName());
        }
        postService.savePost(post);

        //글 공감 테이블에 저장
        PostLike postLike = PostLike.builder()
                .postId(post)
                .memberId(memberEmail.get())
                .build();
        postLikeService.savePostLike(postLike);

        //HashTag 테이블 생성
        if (!hashTagDto.getHashTagName().isEmpty()) {
            for (String hashTag : hashTagDto.getHashTagName()) {
                if (hashTagService.duplicateCheckHashTag(hashTag)) {
                    HashTag buildHashTag = HashTag.builder()
                            .hashTagName(hashTag)
                            .hashTagCount(1L)
                            .build();
                    hashTagService.saveHashTag(buildHashTag);
                    log.info("해시태그 {}",hashTag + " 생성");
                } else {
                    Optional<HashTag> byHashTagName = hashTagService.findByHashTagName(hashTag);
                    if (byHashTagName.isPresent()) {
                        Long hashTagCount = byHashTagName.get().getHashTagCount();
                        byHashTagName.get().setHashTagCount(hashTagCount + 1);
                        hashTagService.saveHashTag(byHashTagName.get());
                        log.info("해시태그 {}",hashTag + "의 카운트 1증가");
                    }
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}