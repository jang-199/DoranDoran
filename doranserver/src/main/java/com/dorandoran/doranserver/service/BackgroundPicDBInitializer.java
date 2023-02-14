package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class BackgroundPicDBInitializer {

    @Autowired BackGroundPicServiceImpl backGroundPicService;
    @Autowired
    PostServiceImpl postService;
    @Autowired
    MemberServiceImpl memberService;
    @Autowired
    PolicyTermsCheckImpl policyTermsCheck;
    @Autowired
    PostLikeServiceImpl postLikeService;

    @Autowired
    UserUploadPicServiceImpl userUploadPicService;

    @Value("${background.cnt}")
    Integer max;
    @Value("${background.Store.path}")
    String serverPath;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 500; i++) {
            PolicyTerms build = PolicyTerms.builder().policy1(true).policy2(true).policy3(true).build();
            policyTermsCheck.policyTermsSave(build);
            Member buildMember = Member.builder().policyTermsId(build)
                    .email(i + "@gmail.com")
                    .dateOfBirth(LocalDate.now())
                    .firebaseToken("firebasetoken")
                    .nickname("nickname" + i)
                    .signUpDate(LocalDateTime.now()).build();
            memberService.saveMember(buildMember);//회원 500명 생성

            Post.builder().content("회원" + i + "의 글입니다.")
                    .forMe(false)
                    .location("37.2326962,127.1877412")
                    .postTime(LocalDateTime.now())
                    .memberId(buildMember)
                    .switchPic(ImgType.DefaultBackground)
                    .ImgName(i/5+".png")
                    .build();
        }


    }
}
