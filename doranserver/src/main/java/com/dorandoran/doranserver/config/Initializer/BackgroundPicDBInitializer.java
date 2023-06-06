

package com.dorandoran.doranserver.config.Initializer;

import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import com.dorandoran.doranserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class BackgroundPicDBInitializer {

    @Autowired
    BackGroundPicServiceImpl backGroundPicService;
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
    ReplyService replyService;

    @Autowired
    CommentServiceImpl commentService;
    @Autowired
    HashTagServiceImpl hashTagService;
    @Autowired
    PostHashServiceImpl postHashService;

    @Value("${background.cnt}")
    Integer max;
    @Value("${background.Store.path}")
    String serverPath;

    @PostConstruct
    public void init() {

        List<String> tagList = List.of("고민", "연애", "10대", "20대", "30대", "40대", "50대", "친구", "남사친", "여사친", "일상", "대화", "짝사랑", "학교", "출근", "ootd");
        tagList.iterator().forEachRemaining(t->hashTagService.saveHashTag(HashTag.builder().hashTagName(t).hashTagCount(0L).build()));

        for (int i = 0; i < 100; i++) { //사진 경로 저장
            BackgroundPic build = BackgroundPic.builder().serverPath(serverPath + (i + 1) + ".jpg").imgName((i + 1) + ".jpg").build();
            backGroundPicService.saveBackgroundPic(build);
        }

        for (Long i = 1L; i <= 50L; i++) {
            PolicyTerms build = PolicyTerms.builder().policy1(true).policy2(true).policy3(true).build();
            policyTermsCheck.policyTermsSave(build); //권한 동의 저장

            Member buildMember = Member.builder()
                    .policyTermsId(build)
                    .email(i + "@gmail.com")
                    .dateOfBirth(LocalDate.now())
                    .firebaseToken("firebasetoken")
                    .nickname("nickname" + i)
                    .signUpDate(LocalDateTime.now())
                    .refreshToken("refresh").build();
            memberService.saveMember(buildMember);//회원 50명 생성

            if(i == 1L){ //1번은 테스트용 계정 생성
                Member build1 = Member.builder()
                        .policyTermsId(build)
                        .email("9643us@naver.com")
                        .dateOfBirth(LocalDate.now())
                        .firebaseToken("firebasetoken")
                        .nickname("nickname" + i)
                        .signUpDate(LocalDateTime.now())
                        .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjg0NDA5NDA1LCJleHAiOjE2OTk5NjE0MDUsInN1YiI6Ijk2NDN1c0BuYXZlci5jb20iLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.vCIAAArYtRL4aKTjxxddHlY5PcJ6E_QQMO2Fuj-XKyM").build();
                memberService.saveMember(build1);
            }

            Post post = Post.builder().content("회원" + i + "의 글입니다.")
                    .forMe(false)
                    .latitude(127.1877412)
                    .longitude(127.1877412)
                    .postTime(LocalDateTime.now())
                    .memberId(buildMember)
                    .switchPic(ImgType.DefaultBackground)
                    .ImgName((i % 100 + 1) + ".jpg")
                    .font("Jua")
                    .fontColor("black")
                    .fontSize(20)
                    .fontBold(400)
                    .build();
            if (i % 2 == 0) {
                post.setAnonymity(Boolean.TRUE);
            } else {
                post.setAnonymity(Boolean.FALSE);
            }
            postService.savePost(post); //글 50개 생성

            //popular에 데이터 추가하고 테스트 진행
            PostLike buildPostLike = PostLike.builder().memberId(buildMember).postId(postService.findSinglePost(i).get()).build();
            postLikeService.savePostLike(buildPostLike);

            Comment comment = Comment.builder()
                    .comment("나는" + i + "야 반가워")
                    .commentTime(LocalDateTime.now())
                    .postId(postService.findSinglePost(i).get())
                    .memberId(buildMember)
                    .countReply(1)
                    .build();
            if (i % 2 == 0) {
                comment.setAnonymity(Boolean.TRUE);
                comment.setCheckDelete(Boolean.TRUE);
            } else {
                comment.setAnonymity(Boolean.FALSE);
                comment.setCheckDelete(Boolean.FALSE);
            }
            commentService.saveComment(comment);

            Reply reply = Reply.builder()
                    .commentId(comment)
                    .memberId(buildMember)
                    .reply("대댓글" + i + "입니다")
                    .ReplyTime(LocalDateTime.now())
                    .build();

            if (i % 2 == 0) {
                reply.setAnonymity(Boolean.TRUE);
                reply.setCheckDelete(Boolean.TRUE);
                reply.setSecretMode(Boolean.TRUE);
            } else {
                reply.setAnonymity(Boolean.FALSE);
                reply.setCheckDelete(Boolean.FALSE);
                reply.setSecretMode(Boolean.FALSE);
            }
            replyService.saveReply(reply);

            HashTag build1 = HashTag.builder().hashTagName("해시태그" + i).hashTagCount(i).build();
            hashTagService.saveHashTag(build1);

            Random random = new Random();
            for (int a = 0; a < random.nextInt(14); a++) {

                for (int a1 = 0; a1 < random.nextInt(1); a1++) {
                    postHashService.savePostHash(PostHash.builder().postId(post).hashTagId(hashTagService.findByHashTagName(tagList.get(a+a1)).get()).build());
                }
            }
        }


    }
}
