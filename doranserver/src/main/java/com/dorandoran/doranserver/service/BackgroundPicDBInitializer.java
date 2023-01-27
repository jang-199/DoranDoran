package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;
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
    @Autowired
    PostLikeServiceImpl postLikeService;
    @Value("${background.cnt}")
    Integer max;
    @Value("${background.Store.path}")
    String serverPath;


//    @PostConstruct
//    public void testData(){
//
//        PolicyTerms build = PolicyTerms.builder().policy1(Boolean.TRUE).policy2(Boolean.TRUE).policy3(Boolean.TRUE).build();
//        policyTermsCheck.policyTermsSave(build);
//
//        Member member1 = Member.builder()
//                .email("1")
//                .dateOfBirth(LocalDate.now())
//                .nickname("1")
//                .firebaseToken("1")
//                .signUpDate(LocalDateTime.now())
//                .policyTermsId(build)
//                .build();
//
//        Member member2 = Member.builder()
//                .email("2")
//                .dateOfBirth(LocalDate.now())
//                .nickname("2")
//                .firebaseToken("1")
//                .signUpDate(LocalDateTime.now())
//                .policyTermsId(build)
//                .build();
//
//        Member member3 = Member.builder()
//                .email("3")
//                .dateOfBirth(LocalDate.now())
//                .nickname("3")
//                .firebaseToken("1")
//                .signUpDate(LocalDateTime.now())
//                .policyTermsId(build)
//                .build();
//
//        memberService.saveMember(member1);
//        memberService.saveMember(member2);
//        memberService.saveMember(member3);
//
//        Post post1 = Post.builder().
//                postTime(LocalDateTime.now())
//                .postId(1L)
//                .forMe(Boolean.FALSE)
//                .content("1")
//                .location("1")
//                .switchPic(ImgType.DefaultBackground)
//                .ImgName("6.jpg")
//                .memberId(member1)
//                .build();
//
//        Post post2 = Post.builder().
//                postTime(LocalDateTime.now())
//                .postId(2L)
//                .forMe(Boolean.FALSE)
//                .content("2")
//                .location("1")
//                .switchPic(ImgType.DefaultBackground)
//                .ImgName("6.jpg")
//                .memberId(member2)
//                .build();
//
//        Post post3 = Post.builder().
//                postTime(LocalDateTime.now())
//                .postId(3L)
//                .forMe(Boolean.FALSE)
//                .content("3")
//                .location("1")
//                .switchPic(ImgType.UserUpload)
//                .ImgName("asfjdiojasfiodfasd.jpg")
//                .memberId(member3)
//                .build();
//        postService.savePost(post1);
//        postService.savePost(post2);
//        postService.savePost(post3);
//
//        //멤버1
//        PostLike build1 = PostLike.builder().postId(post1).memberId(member1).build();
//
//        //멤버2
//        PostLike build2 = PostLike.builder().postId(post1).memberId(member2).build();
//        PostLike build3 = PostLike.builder().postId(post2).memberId(member2).build();
//
//        //멤버3
//        PostLike build4 = PostLike.builder().postId(post1).memberId(member3).build();
//        PostLike build5 = PostLike.builder().postId(post2).memberId(member3).build();
//        PostLike build6 = PostLike.builder().postId(post3).memberId(member3).build();
//
//        postLikeService.savePostLike(build1);
//        postLikeService.savePostLike(build2);
//        postLikeService.savePostLike(build3);
//        postLikeService.savePostLike(build4);
//        postLikeService.savePostLike(build5);
//        postLikeService.savePostLike(build6);
//    }


    @PostConstruct
    public void init() {

//        for (int i = 0; i < max; i++) {
//            BackgroundPic build = BackgroundPic.builder().imgName(i+1 + ".jpg")
//                    .serverPath(serverPath + (i+1) + ".jpg")
//                    .build();
//            backGroundPicService.saveBackgroundPic(build);
//        }
//
//
//
//        for (int i = 1; i <= 100; i++) {
//
//            PolicyTerms build1 = PolicyTerms.builder().policy1(true)
//                    .policy2(true)
//                    .policy3(true)
//                    .build();
//            policyTermsCheck.policyTermsSave(build1);
//
//            Member build2 = Member.builder().email("email")
//                    .dateOfBirth(LocalDate.now())
//                    .nickname("nickname")
//                    .signUpDate(LocalDateTime.now())
//                    .firebaseToken("token")
//                    .policyTermsId(build1)
//                    .build();
//            memberService.saveMember(build2);
//
//            if ((i%2)!=0){
//                Post build = Post.builder().postTime(LocalDateTime.now()).content("hi"+i)
//                        .forMe(true)
//                        .location("location")
//                        .memberId(build2)
//                        .switchPic(ImgType.UserUpload)
//                        .ImgName("img")
//                        .build();
//                postService.savePost(build);
//            }else{
//                Post build = Post.builder().postTime(LocalDateTime.now()).content("hi"+i)
//                        .forMe(false)
//                        .location("location")
//                        .memberId(build2)
//                        .switchPic(ImgType.UserUpload)
//                        .ImgName("img")
//                        .build();
//                postService.savePost(build);
//            }
//
//        }

//        for (int i = 1; i <= 400; i++) {
//
//            PolicyTerms build1 = PolicyTerms.builder().policy1(true)
//                    .policy2(true)
//                    .policy3(true)
//                    .build();
//            policyTermsCheck.policyTermsSave(build1);
//
//            Member build2 = Member.builder().email("email")
//                    .dateOfBirth(LocalDate.now())
//                    .nickname("nickname")
//                    .signUpDate(LocalDateTime.now())
//                    .firebaseToken("token")
//                    .policyTermsId(build1)
//                    .build();
//            memberService.saveMember(build2);
//
//            Post build = Post.builder().postTime(LocalDateTime.now()).content("hi"+i)
//                    .forMe(false)
//                    .location("location")
//                    .memberId(build2)
//                    .switchPic(ImgType.UserUpload)
//                    .ImgName("img")
//                    .build();
//            postService.savePost(build);
//        }
//
    }
}
