package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.BackgroundPic;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${background.cnt}")
    Integer max;

    @Value("${background.Store.path}")
    String serverPath;

    @PostConstruct
    public void init() {
        for (int i = 0; i < max; i++) {
            BackgroundPic build = BackgroundPic.builder().imgName(i+1 + ".jpg")
                    .serverPath(serverPath + (i+1) + ".jpg")
                    .build();
            backGroundPicService.saveBackgroundPic(build);
        }

        for (int i = 1; i <= 400; i++) {

            PolicyTerms build1 = PolicyTerms.builder().policy1(true)
                    .policy2(true)
                    .policy3(true)
                    .build();
            policyTermsCheck.policyTermsSave(build1);

            Member build2 = Member.builder().email("email")
                    .dateOfBirth(LocalDate.now())
                    .nickname("nickname")
                    .signUpDate(LocalDateTime.now())
                    .firebaseToken("token")
                    .policyTermsId(build1)
                    .build();
            memberService.saveMember(build2);

            Post build = Post.builder().postTime(new Date()).content("hi"+i)
                    .forMe(false)
                    .deleteDate(null)
                    .location("location")
                    .memberId(build2)
                    .switchPic(ImgType.UserUpload)
                    .ImgName("img")
                    .build();
            postService.savePost(build);
        }
    }
}
