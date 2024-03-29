//
//
//package com.dorandoran.doranserver.global.config.Initializer;
//
//import com.dorandoran.doranserver.domain.background.domain.BackgroundPic;
//import com.dorandoran.doranserver.domain.background.service.BackgroundPicService;
//import com.dorandoran.doranserver.domain.background.service.UserUploadPicService;
//import com.dorandoran.doranserver.domain.comment.domain.Comment;
//import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
//import com.dorandoran.doranserver.domain.comment.domain.Reply;
//import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
//import com.dorandoran.doranserver.domain.comment.service.CommentService;
//import com.dorandoran.doranserver.domain.comment.service.ReplyService;
//import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
//import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
//import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
//import com.dorandoran.doranserver.domain.member.domain.Member;
//import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
//import com.dorandoran.doranserver.domain.member.service.*;
//import com.dorandoran.doranserver.domain.notification.domain.NotificationHistory;
//import com.dorandoran.doranserver.domain.notification.domain.notificationType.NotificationType;
//import com.dorandoran.doranserver.domain.notification.service.NotificationHistoryService;
//import com.dorandoran.doranserver.domain.post.domain.Post;
//import com.dorandoran.doranserver.domain.post.domain.PostLike;
//import com.dorandoran.doranserver.domain.post.service.*;
//import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
//import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
//import com.dorandoran.doranserver.domain.member.repository.LockMemberRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//@Slf4j
//@Profile("local")
//@Component
//public class BackgroundPicDBInitializer {
//
//    @Autowired
//    BackgroundPicService backGroundPicService;
//    @Autowired
//    PostService postService;
//    @Autowired
//    MemberService memberService;
//    @Autowired
//    PolicyTermsCheck policyTermsCheck;
//    @Autowired
//    PostLikeService postLikeService;
//
//    @Autowired
//    UserUploadPicService userUploadPicService;
//    @Autowired
//    ReplyService replyService;
//
//    @Autowired
//    CommentService commentService;
//    @Autowired
//    HashTagService hashTagService;
//    @Autowired
//    PostHashService postHashService;
//    @Autowired
//    AccountClosureMemberService accountClosureMemberService;
//    @Autowired
//    NotificationHistoryService notificationHistoryService;
//    @Autowired
//    LockMemberRepository lockMemberRepository;
//    @Autowired
//    CommentLikeService commentLikeService;
//
//    @Value("${background.cnt}")
//    Integer max;
//    @Value("${background.Store.path}")
//    String serverPath;
//
//    @PostConstruct
//    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        log.info("initinit");

//        List<String> hashtagList = setHashtag();
//        setBackgroundPic();//사진 경로 저장

//        for (long i = 1L; i <= 22L; i++) {
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
//                        .nickname("Admin").checkNotification(true)
//                        .signUpDate(LocalDateTime.now())
//                        .checkNotification(Boolean.TRUE)
//                        .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
//                        .build();
//                build1.setOsType(OsType.Ios);
//                memberService.saveMember(build1);
//
//                Map<NotificationType, String> message = Map.of(
//                        NotificationType.PostLike,"글 좋아요",
//                        NotificationType.CommentLike, "댓글 좋아요",
//                        NotificationType.Comment, "댓글 생성",
//                        NotificationType.Reply, "대댓글 생성");
//
//                for (Map.Entry<NotificationType, String> notificationTypeStringEntry : message.entrySet()) {
//                    NotificationHistory notificationHistory1 = NotificationHistory.builder()
//                            .message(notificationTypeStringEntry.getValue())
//                            .objectId(1L)
//                            .notificationType(notificationTypeStringEntry.getKey())
//                            .memberId(build1)
//                            .build();
//
//                    NotificationHistory notificationHistory2 = NotificationHistory.builder()
//                            .message(notificationTypeStringEntry.getValue())
//                            .objectId(1L)
//                            .notificationType(notificationTypeStringEntry.getKey())
//                            .memberId(build1)
//                            .build();
//
//                    notificationHistory1.setNotificationReadTime(LocalDateTime.now());
//
//                    notificationHistoryService.saveNotification(notificationHistory1);
//                    notificationHistoryService.saveNotification(notificationHistory2);
//                }
//            }
//            Boolean locked1 = i%2 == 0 ? Boolean.FALSE : Boolean.TRUE;
//            PolicyTerms policyTerms = setPolicyTerms();//권한 동의 저장
//            Member member = setMember(policyTerms, i + "@gmail.com", "nickname" + i, OsType.Ios);//맴버 생성 후 저장
//            Post post = setPost(i + "번 글입니다.", Boolean.FALSE, member, locked1);//글 생성 후 저장
//
//            if (i % 2 == 1) {
//                PostLike postLike = PostLike.builder().postId(post)
//                        .memberId(memberService.findByEmail("9643us@naver.com"))
//                        .checkDelete(false)
//                        .build();
//                postLikeService.savePostLike(postLike);
//            }
//
//            for (long j = 1L; j < i; j++) {
//                Boolean locked = j%2 == 0 ? Boolean.FALSE : Boolean.TRUE;
//
//                setPostLike(post,memberService.findByEmail(j+"@gmail.com"));
//                Comment comment = setComment(post, member, "contents", Boolean.FALSE, locked);//댓글 생성 후 저장
//
//                if (j == 1L) {
//                    CommentLike commentLike = CommentLike.builder()
//                            .checkDelete(Boolean.FALSE)
//                            .memberId(member)
//                            .commentId(comment)
//                            .build();
//
//                    commentLikeService.saveCommentLike(commentLike);
//                }
//
//                for (long k = 1L; k < i; k++) {
//                    Reply reply = setReply(comment, member, "댓글", Boolean.FALSE, locked);//대댓글 생성 후 저장
//                }
//            }
//
//            Random random = new Random();
//            for (int j = 0; j <= random.nextInt(2); j++) {
//
//                int randomNum = random.nextInt(15);
//
//                HashTag hashtag = increaseHashtagCount(hashtagList.get(j + randomNum));
//                PostHash postHash = setPostHash(post, hashtag);
//
//            }
//        }
//    }

//
//    private PostLike setPostLike(Post post, Member member) {
//        PostLike postLike = PostLike.builder()
//                .postId(post)
//                .memberId(member)
//                .checkDelete(Boolean.FALSE)
//                .build();
//        postLikeService.savePostLike(postLike);
//        return postLike;
//    }
//
//    private HashTag increaseHashtagCount(String hashtagName) {
//        HashTag hashtag = hashTagService.findByHashTagName(hashtagName);
//        hashtag.setHashTagCount(hashtag.getHashTagCount() + 1);
//        hashTagService.saveHashTag(hashtag);
//        return hashtag;
//    }
//
//    private void setBackgroundPic() {
//        for (int i = 0; i < 100; i++) { //사진 경로 저장
//            BackgroundPic build = BackgroundPic.builder().serverPath(serverPath + (i + 1) + ".jpg").imgName((i + 1) + ".jpg").build();
//            backGroundPicService.saveBackgroundPic(build);
//        }
//    }
//
//    private List<String> setHashtag() {
//        List<String> hashtagList = List.of("고민", "연애", "10대", "20대", "30대", "40대", "50대", "친구", "남사친", "여사친", "일상", "대화", "짝사랑", "학교", "출근", "ootd");
//        hashtagList.iterator().forEachRemaining(t->hashTagService.saveHashTag(HashTag.builder().hashTagName(t).build()));
//        return hashtagList;
//    }
//
//    private PolicyTerms setPolicyTerms() {
//        PolicyTerms policyTerms = PolicyTerms.builder().policy1(Boolean.TRUE)
//                .policy2(Boolean.TRUE)
//                .policy3(Boolean.TRUE)
//                .build();
//        policyTermsCheck.policyTermsSave(policyTerms);
//        return policyTerms;
//    }
//
//    private Member setMember(PolicyTerms policyTerms,String email,String nickname, OsType osType) {
//        Member member = Member.builder()
//                .policyTermsId(policyTerms)
//                .email(email)
//                .dateOfBirth(LocalDate.now())
//                .firebaseToken("cekww1BXT4GCJM8cvkw45v:APA91bEJ0Z_iZXwKoIjIxLvb9X4g9AOCBpsLtjujy5jlmRZsboFVx6TM0Av9ChDNivcuY_zhREf_ClAN4BIr2qPXMNQctwqtVmRzpadv6fwbdoHwoNZf7numWEFYzYrltOVHnNmJBUZc")
//                .nickname(nickname)
//                .closureDate(LocalDate.of(2000,12,12))
//                .signUpDate(LocalDateTime.now())
//                .checkNotification(true)
//                .refreshToken("refreshTokenByInitializer")
//                .osType(osType)
//                .build();
//        memberService.saveMember(member);
//        return member;
//    }
//
//    private Post setPost(String contents, Boolean anonymity, Member member, Boolean locked) {
//        long round = Math.round(Math.random() * 100 + 1);
//        int i = (int) round;
//
//        GeometryFactory geometryFactory = new GeometryFactory();
//        Coordinate coordinate = new Coordinate(37.31681241549904, 127.08951837477541);
//        Point point = geometryFactory.createPoint(coordinate);
//
//        Post post = Post.builder().content(contents)
//                .forMe(false)
//                .location(point)
//                .memberId(member)
//                .switchPic(ImgType.DefaultBackground)
//                .ImgName(i + ".jpg")
//                .font("Jua")
//                .fontColor("black")
//                .fontSize(20)
//                .fontBold(400)
//                .reportCount(0)
//                .anonymity(anonymity)
//                .isLocked(locked)
//                .build();
//        postService.savePost(post);
//        return post;
//    }
//
//    private Comment setComment(Post post, Member member, String contents, Boolean anonymity, Boolean locked) {
//        Comment comment = Comment.builder()
//                .comment(contents)
//                .postId(post)
//                .memberId(member)
//                .countReply(1)
//                .reportCount(0)
//                .isLocked(locked)
//                .anonymity(anonymity)
//                .checkDelete(false)
//                .secretMode(false)
//                .build();
//        commentService.saveComment(comment);
//        return comment;
//    }
//
//    private Reply setReply(Comment comment, Member buildMember, String contents, Boolean anonymity, Boolean locked) {
//        Reply reply = Reply.builder()
//                .commentId(comment)
//                .memberId(buildMember)
//                .reply(contents)
//                .reportCount(0)
//                .isLocked(locked)
//                .checkDelete(false)
//                .anonymity(anonymity)
//                .secretMode(false)
//                .build();
//        replyService.saveReply(reply);
//        return reply;
//    }
//
//    private PostHash setPostHash(Post post, HashTag hashtag) {
//        PostHash postHash = PostHash.builder()
//                .postId(post)
//                .hashTagId(hashtag)
//                .build();
//        postHashService.savePostHash(postHash);
//        return postHash;
//    }
//}