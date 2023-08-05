//
//
//package com.dorandoran.doranserver.config.Initializer;
//
//import com.dorandoran.doranserver.dto.Jackson2JsonRedisDto;
//import com.dorandoran.doranserver.entity.*;
//import com.dorandoran.doranserver.entity.imgtype.ImgType;
//import com.dorandoran.doranserver.entity.osType.OsType;
//import com.dorandoran.doranserver.service.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.UrlResource;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Random;
//
//@Slf4j
//@Component
//public class BackgroundPicDBInitializer {
//
//    @Autowired
//    BackGroundPicServiceImpl backGroundPicService;
//    @Autowired
//    PostServiceImpl postService;
//    @Autowired
//    MemberServiceImpl memberService;
//    @Autowired
//    PolicyTermsCheckImpl policyTermsCheck;
//    @Autowired
//    PostLikeServiceImpl postLikeService;
//
//    @Autowired
//    UserUploadPicServiceImpl userUploadPicService;
//    @Autowired
//    ReplyService replyService;
//
//    @Autowired
//    CommentServiceImpl commentService;
//    @Autowired
//    HashTagServiceImpl hashTagService;
//    @Autowired
//    PostHashServiceImpl postHashService;
//
//    @Autowired
//    AccountClosureMemberService accountClosureMemberService;
//    @Value("${background.cnt}")
//    Integer max;
//    @Value("${background.Store.path}")
//    String serverPath;
//    @Autowired
//    private RedisTemplate<String, Jackson2JsonRedisDto> redisTemplate;
//
//    @PostConstruct
//    public void init() throws IOException {
//
//        List<String> tagList = List.of("고민", "연애", "10대", "20대", "30대", "40대", "50대", "친구", "남사친", "여사친", "일상", "대화", "짝사랑", "학교", "출근", "ootd");
//        tagList.iterator().forEachRemaining(t->hashTagService.saveHashTag(HashTag.builder().hashTagName(t).hashTagCount(0L).build()));
//
//        for (int i = 0; i < 100; i++) { //사진 경로 저장
//            BackgroundPic build = BackgroundPic.builder().serverPath(serverPath + (i + 1) + ".jpg").imgName((i + 1) + ".jpg").build();
//            backGroundPicService.saveBackgroundPic(build);
//        }
//
//        for (Long i = 1L; i <= 50L; i++) {
//            PolicyTerms build = PolicyTerms.builder().policy1(true).policy2(true).policy3(true).build();
//            policyTermsCheck.policyTermsSave(build); //권한 동의 저장
//
//            Member buildMember = Member.builder()
//                    .policyTermsId(build)
//                    .email(i + "@gmail.com")
//                    .dateOfBirth(LocalDate.now())
//                    .firebaseToken("cekww1BXT4GCJM8cvkw45v:APA91bEJ0Z_iZXwKoIjIxLvb9X4g9AOCBpsLtjujy5jlmRZsboFVx6TM0Av9ChDNivcuY_zhREf_ClAN4BIr2qPXMNQctwqtVmRzpadv6fwbdoHwoNZf7numWEFYzYrltOVHnNmJBUZc")
//                    .nickname("nickname" + i)
//                    .closureDate(LocalDate.of(2000,12,12))
//                    .signUpDate(LocalDateTime.now())
//                    .refreshToken("refresh").build();
//            if (i % 2 == 0) {
//                buildMember.setOsType(OsType.Ios);
//            } else {
//                buildMember.setOsType(OsType.Aos);
//            }
//            memberService.saveMember(buildMember);//회원 50명 생성
////            AccountClosureMember build2 = AccountClosureMember.builder().closureMemberId(buildMember).build();
////            accountClosureMemberService.saveClosureMember(build2);
//
//
//            if(i == 1L){ //1번은 테스트용 계정 생성
//                PolicyTerms build3 = PolicyTerms.builder().policy1(true).policy2(true).policy3(true).build();
//                policyTermsCheck.policyTermsSave(build3);
//                Member build1 = Member.builder()
//                        .policyTermsId(build3)
//                        .email("9643us@naver.com")
//                        .dateOfBirth(LocalDate.now())
//                        .firebaseToken("firebasetoken")
//                        .closureDate(LocalDate.of(2000,12,12))
//                        .nickname("xcvfdsfs")
//                        .signUpDate(LocalDateTime.now())
//                        .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjg2MzE4NzMzLCJleHAiOjE3MDE4NzA3MzMsInN1YiI6InhjdmZkc2ZzIiwiZW1haWwiOiI5NjQzdXNAbmF2ZXIuY29tIn0.f1pSfnAwWoOyrK4fa6vBtVh9zZ_8jw99mu7aA8J90Xg")
//                        .build();
//                build1.setOsType(OsType.Ios);
//                memberService.saveMember(build1);
////                AccountClosureMember build4 = AccountClosureMember.builder().closureMemberId(build1).build();
////                accountClosureMemberService.saveClosureMember(build4);
//            }
//
//            Post post = Post.builder().content("회원" + i + "의 글입니다.")
//                    .forMe(false)
//                    .latitude(127.1877412)
//                    .longitude(127.1877412)
//                    .postTime(LocalDateTime.now())
//                    .memberId(buildMember)
//                    .switchPic(ImgType.DefaultBackground)
//                    .ImgName((i % 100 + 1) + ".jpg")
//                    .font("Jua")
//                    .fontColor("black")
//                    .fontSize(20)
//                    .fontBold(400)
//                    .reportCount(0)
//                    .isLocked(Boolean.FALSE)
//                    .build();
//            if (i % 2 == 0) {
//                post.setAnonymity(Boolean.TRUE);
//            } else {
//                post.setAnonymity(Boolean.FALSE);
//            }
//            postService.savePost(post); //글 50개 생성
//
///*            //popular에 데이터 추가하고 테스트 진행
//            PostLike buildPostLike = PostLike.builder().memberId(buildMember).postId(postService.findSinglePost(i)).build();
//            postLikeService.savePostLike(buildPostLike);*/
//
//            Comment comment = Comment.builder()
//                    .comment("나는" + i + "야 반가워")
//                    .commentTime(LocalDateTime.now())
//                    .postId(postService.findSinglePost(i))
//                    .memberId(buildMember)
//                    .countReply(1)
//                    .reportCount(0)
//                    .isLocked(Boolean.FALSE)
//                    .build();
//            if (i % 2 == 0) {
//                comment.setAnonymity(Boolean.TRUE);
//                comment.setCheckDelete(Boolean.TRUE);
//            } else {
//                comment.setAnonymity(Boolean.FALSE);
//                comment.setCheckDelete(Boolean.FALSE);
//            }
//            commentService.saveComment(comment);
//
//            Reply reply = Reply.builder()
//                    .commentId(comment)
//                    .memberId(buildMember)
//                    .reply("대댓글" + i + "입니다")
//                    .ReplyTime(LocalDateTime.now())
//                    .reportCount(0)
//                    .isLocked(Boolean.FALSE)
//                    .build();
//
//            if (i % 2 == 0) {
//                reply.setAnonymity(Boolean.TRUE);
//                reply.setCheckDelete(Boolean.TRUE);
//                reply.setSecretMode(Boolean.TRUE);
//            } else {
//                reply.setAnonymity(Boolean.FALSE);
//                reply.setCheckDelete(Boolean.FALSE);
//                reply.setSecretMode(Boolean.FALSE);
//            }
//            replyService.saveReply(reply);
//
//            Random random = new Random();
//            for (int a = 0; a <= random.nextInt(2); a++) {
//
//                int a1 = random.nextInt(15);
//                log.info("postId: {} , hashTag: {}",post.getContent(),hashTagService.findByHashTagName(tagList.get(a+a1)));
//                HashTag hashTag = hashTagService.findByHashTagName(tagList.get(a + a1));
//                hashTag.setHashTagCount(hashTag.getHashTagCount()+1L);
//                hashTagService.saveHashTag(hashTag);
//                postHashService.savePostHash(PostHash.builder().postId(post).hashTagId(hashTag).build());
//
//            }
//        }
//
//        for (int i = 1; i < 101; i++) {
//            redisTemplate.opsForValue().set(String.valueOf(i), Jackson2JsonRedisDto.builder().pic(new UrlResource("file:" + serverPath + i  + ".jpg").getContentAsByteArray()).FileName(i+".jpg").build());
//            //1.jpg
//        }
//
//
//    }
//}
