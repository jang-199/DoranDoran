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

    @Autowired
    CommentServiceImpl commentService;

    @Value("${background.cnt}")
    Integer max;
    @Value("${background.Store.path}")
    String serverPath;
    @PostConstruct
    public void init() {

        for (int i = 0; i < 100; i++) {
            BackgroundPic build = BackgroundPic.builder().serverPath(serverPath + (i + 1) + ".png").imgName((i + 1) + ".png").build();
            backGroundPicService.saveBackgroundPic(build);
        }

        for (Long i = 1L; i <= 500L; i++) {
            PolicyTerms build = PolicyTerms.builder().policy1(true).policy2(true).policy3(true).build();
            policyTermsCheck.policyTermsSave(build);
            Member buildMember = Member.builder().policyTermsId(build)
                    .email(i + "@gmail.com")
                    .dateOfBirth(LocalDate.now())
                    .firebaseToken("firebasetoken")
                    .nickname("nickname" + i)
                    .signUpDate(LocalDateTime.now()).build();
            memberService.saveMember(buildMember);//회원 500명 생성

            Post post = Post.builder().content("회원" + i + "의 글입니다.")
                    .forMe(false)
                    .latitude("127.1877412")
                    .longitude("127.1877412")
                    .postTime(LocalDateTime.now())
                    .memberId(buildMember)
                    .switchPic(ImgType.DefaultBackground)
                    .ImgName((i % 100 + 1) + ".png")
                    .font("Jua")
                    .fontColor("black")
                    .fontSize(48)
                    .fontBold(900)
                    .build();
            postService.savePost(post); //글 500개 생성

            for (Long j = i; j >0 ; j--) {
                PostLike buildPostLike = PostLike.builder().memberId(buildMember).postId(postService.findSinglePost(i).get()).build();
                postLikeService.savePostLike(buildPostLike);

                Comment comment = Comment.builder()
                        .comment("나는" + i + "야 반가워")
                        .commentTime(LocalDateTime.now())
                        .postId(postService.findSinglePost(j).get())
                        .memberId(buildMember).build();
                commentService.saveComment(comment);
            }
            //popular에 데이터 추가하고 테스트 진행

        }


    }

    @PostConstruct
    public void testData(){
        UserUploadPic build8 = UserUploadPic.builder().imgName("6.jpg").serverPath(serverPath + "6.jpg").build();
        userUploadPicService.saveUserUploadPic(build8);

        PolicyTerms build = PolicyTerms.builder().policy1(Boolean.TRUE).policy2(Boolean.TRUE).policy3(Boolean.TRUE).build();
        policyTermsCheck.policyTermsSave(build);

        Member member1 = Member.builder()
                .email("1")
                .dateOfBirth(LocalDate.now())
                .nickname("1")
                .firebaseToken("1")
                .signUpDate(LocalDateTime.now())
                .policyTermsId(build)
                .build();

        Member member2 = Member.builder()
                .email("2")
                .dateOfBirth(LocalDate.now())
                .nickname("2")
                .firebaseToken("1")
                .signUpDate(LocalDateTime.now())
                .policyTermsId(build)
                .build();

        Member member3 = Member.builder()
                .email("3")
                .dateOfBirth(LocalDate.now())
                .nickname("3")
                .firebaseToken("1")
                .signUpDate(LocalDateTime.now())
                .policyTermsId(build)
                .build();

        memberService.saveMember(member1);
        memberService.saveMember(member2);
        memberService.saveMember(member3);

        Post post1 = Post.builder().
                postTime(LocalDateTime.now())
                .postId(1L)
                .forMe(Boolean.FALSE)
                .content("1")
                .latitude("127.1877412")
                .longitude("127.1877412")
                .switchPic(ImgType.DefaultBackground)
                .ImgName("6.jpg")
                .memberId(member1)
                .font("Jua")
                .fontColor("black")
                .fontSize(48)
                .fontBold(900)
                .build();

        Post post2 = Post.builder().
                postTime(LocalDateTime.now())
                .postId(2L)
                .forMe(Boolean.FALSE)
                .content("2")
                .latitude("127.1877412")
                .longitude("127.1877412")
                .switchPic(ImgType.DefaultBackground)
                .ImgName("6.jpg")
                .memberId(member2)
                .font("Jua")
                .fontColor("black")
                .fontSize(48)
                .fontBold(900)
                .build();

        Post post3 = Post.builder().
                postTime(LocalDateTime.now())
                .postId(3L)
                .forMe(Boolean.FALSE)
                .content("3")
                .latitude("127.1877412")
                .longitude("127.1877412")
                .switchPic(ImgType.UserUpload)
                .ImgName("6.jpg")
                .memberId(member3)
                .font("Jua")
                .fontColor("black")
                .fontSize(48)
                .fontBold(900)
                .build();
        postService.savePost(post1);
        postService.savePost(post2);
        postService.savePost(post3);

        //멤버1
        PostLike build1 = PostLike.builder().postId(post1).memberId(member1).build();

        //멤버2
        PostLike build2 = PostLike.builder().postId(post1).memberId(member2).build();
        PostLike build3 = PostLike.builder().postId(post2).memberId(member2).build();

        //멤버3
        PostLike build4 = PostLike.builder().postId(post1).memberId(member3).build();
        PostLike build5 = PostLike.builder().postId(post2).memberId(member3).build();
        PostLike build6 = PostLike.builder().postId(post3).memberId(member3).build();

        postLikeService.savePostLike(build1);
        postLikeService.savePostLike(build2);
        postLikeService.savePostLike(build3);
        postLikeService.savePostLike(build4);
        postLikeService.savePostLike(build5);
        postLikeService.savePostLike(build6);


    }



}

