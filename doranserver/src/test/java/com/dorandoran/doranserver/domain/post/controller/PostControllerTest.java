package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.lockType.LockType;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.domain.post.service.AnonymityMemberService;
import com.dorandoran.doranserver.domain.post.service.PostHashService;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.domain.post.service.common.PostCommonService;
import com.dorandoran.doranserver.global.util.CommentResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class PostControllerTest {
    private final String refreshToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E";

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;
    @MockBean
    private PostLikeService postLikeService;
    @MockBean
    private PostService postService;
    @MockBean
    private PostHashService postHashService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ReplyService replyService;
    @MockBean
    private AnonymityMemberService anonymityMemberService;
    @MockBean
    private LockMemberService lockMemberService;
    @MockBean
    private PostCommonService postCommonService;
    @MockBean
    private MemberBlockListService memberBlockListService;

    @Test
    void savePost() throws Exception {
        //given
        Member member = setMember1();
        Optional<LockMember> lockMember = Optional.of(new LockMember(member, LocalDateTime.now(), LocalDateTime.now().plusDays(1L), LockType.Day1));
        String backgroundPicContent = new ObjectMapper().writeValueAsString(setBackgroundPicPostDto());
        String userUploadPicContent = new ObjectMapper().writeValueAsString(setUserUploadPicPostDto());
        //todo multipartfile은 어케 직렬화하누..
        Post post = setPost(setLocation(), member);

        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(lockMemberService.findLockMember(BDDMockito.any())).willReturn(lockMember);
        BDDMockito.given(lockMemberService.checkCurrentLocked(BDDMockito.any())).willReturn(Boolean.FALSE).willReturn(Boolean.FALSE).willReturn(Boolean.TRUE);
        BDDMockito.doNothing().when(lockMemberService).deleteLockMember(lockMember.get());
        BDDMockito.given(postService.findSinglePost(BDDMockito.any())).willReturn(post);

        //when
        ResultActions successByBackgroundPicResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(backgroundPicContent)
        );

        ResultActions successByUserUploadPicResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(userUploadPicContent)
        );

        ResultActions lockMemberCannotCreatePostResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(backgroundPicContent)
        );

        //then
        successByBackgroundPicResult.andExpect(MockMvcResultMatchers.status().isCreated());
        successByUserUploadPicResult.andExpect(MockMvcResultMatchers.status().isCreated());
        lockMemberCannotCreatePostResult.andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @Test
    void postDelete() throws Exception {
        //given
        Post post1 = setPost(setLocation(), setMember1());
        Post post2 = setPost(setLocation(), setMember2());
        PostDto.DeletePost deletePostId = PostDto.DeletePost.builder().postId(1L).build();
        String content = new ObjectMapper().writeValueAsString(deletePostId);
        BDDMockito.given(postService.findFetchMember(BDDMockito.any())).willReturn(post1).willReturn(post2);
        BDDMockito.doNothing().when(postCommonService).deletePost(BDDMockito.any());

        //when
        ResultActions successResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/post")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions forbiddenResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/post")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        forbiddenResult.andExpect(MockMvcResultMatchers.status().isForbidden());
        successResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void postLike() throws Exception {
        //given
        Member member1 = setMember1();
        Member member2 = setMember2();
        Post post1 = setPost(setLocation(), member1);
        Post post2 = setPost(setLocation(), member2);
        PostLike isLivedPostLike = setPostLike(post1, Boolean.FALSE);
        PostLike isDeletedPostLike = setPostLike(post1, Boolean.TRUE);

        PostDto.LikePost likePost = new PostDto.LikePost(1L);
        String content = new ObjectMapper().writeValueAsString(likePost);

        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member1);
        BDDMockito.given(postService.findSinglePost(BDDMockito.any())).willReturn(post1).willReturn(post2).willReturn(post2).willReturn(post2);
        BDDMockito.given(postLikeService.findLikeOne(BDDMockito.any(), BDDMockito.any()))
                .willReturn(Optional.empty())
                .willReturn(Optional.empty())
                .willReturn(Optional.of(isLivedPostLike))
                .willReturn(Optional.of(isDeletedPostLike));

        //when
        ResultActions forbiddenResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/like")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions makePostLikeResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/like")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions postLikeCancelResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/like")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions postLikeRestoreResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/like")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        forbiddenResult.andExpect(MockMvcResultMatchers.status().isForbidden());
        postLikeCancelResult.andExpect(MockMvcResultMatchers.status().isNoContent());
        postLikeRestoreResult.andExpect(MockMvcResultMatchers.status().isNoContent());
        makePostLikeResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }



    @Test
    void postDetails() throws Exception {
        //given
        Member requestMember = setMember1();
        Member postMember = setMember2();
        Member member3 = setMember3();
        List<Member> blockMemberList = List.of(setMember3());
        Post post = setPost(setLocation(), postMember);
        List<Comment> commentList = setCommentList(requestMember, postMember, member3, post);
        List<Reply> replyList = setReplyList(requestMember, postMember, member3, commentList);
        PostDto.ReadPost postDto = new PostDto.ReadPost(1L, "35.123,15.553");
        HashTag hashtag = setHashTag("테스트");
        PostHash postHash = PostHash.builder().postId(post).hashTagId(hashtag).build();
        String content = new ObjectMapper().writeValueAsString(postDto);

        BDDMockito.given(postService.findFetchMember(BDDMockito.any())).willReturn(post);
        BDDMockito.given(anonymityMemberService.findAllUserEmail(BDDMockito.any())).willReturn(new ArrayList<>());
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(requestMember);
        BDDMockito.given(memberBlockListService.findMemberBlockListByBlockingMember(BDDMockito.any())).willReturn(new ArrayList<>()).willReturn(blockMemberList);
        BDDMockito.given(postLikeService.findLIkeCnt(BDDMockito.any())).willReturn(9);
        BDDMockito.given(postLikeService.findLikeResult(BDDMockito.any(), BDDMockito.any())).willReturn(Boolean.FALSE);
        BDDMockito.given(commentService.findCommentByPost(BDDMockito.any())).willReturn(new ArrayList<>());
        BDDMockito.given(replyService.findReplyByCommentList(BDDMockito.any())).willReturn(new ArrayList<>());
        BDDMockito.given(commentService.findCommentAndReplyCntByPostId(BDDMockito.any(), BDDMockito.any())).willReturn(100);
        BDDMockito.given(postService.isCommentReplyAuthor(BDDMockito.any(), BDDMockito.any(), BDDMockito.any())).willReturn(Boolean.TRUE);
        BDDMockito.given(commentService.findFirstCommentsFetchMember(BDDMockito.any())).willReturn(commentList);
        BDDMockito.given(commentService.checkExistAndDelete(BDDMockito.any())).willReturn(Boolean.TRUE);
        BDDMockito.given(replyService.findRankRepliesByComments(BDDMockito.any())).willReturn(replyList);
        BDDMockito.given(postHashService.findPostHash(BDDMockito.any())).willReturn(List.of(postHash));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/detail")
                        .header("Authorization", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$['content']").value("테스트입니다."));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$['commentCnt']").value(100));
    }

    private static HashTag setHashTag(String hashtagName) {
        return HashTag.builder().hashTagName(hashtagName).hashTagCount(1L).build();
    }

    private List<Reply> setReplyList(Member member1, Member member2, Member member3, List<Comment> commentList) {
        ArrayList<Reply> replyList = new ArrayList<>();
        List<Member> memberList = List.of(member1, member2, member3);
        for (Comment comment : commentList) {
            for (int i = 0; i<10; i++) {
                Reply reply = Reply.builder()
                        .reply("테스트 대댓글입니다.")
                        .commentId(comment)
                        .memberId(memberList.get(new Random().nextInt(3)))
                        .anonymity(Boolean.FALSE)
                        .secretMode(Boolean.FALSE)
                        .checkDelete(Boolean.FALSE)
                        .isLocked(Boolean.FALSE)
                        .reportCount(0)
                        .build();

                replyList.add(reply);
            }

        }
        return replyList;
    }

    private List<Comment> setCommentList(Member member1, Member member2, Member member3, Post post) {
        ArrayList<Comment> commentList = new ArrayList<>();
        List<Member> memberList = List.of(member1, member2, member3);
        for (int i = 0; i<10; i++){
            Comment comment = Comment.builder()
                    .comment("테스트 댓글입니다.")
                    .secretMode(Boolean.FALSE)
                    .checkDelete(Boolean.FALSE)
                    .anonymity(Boolean.FALSE)
                    .reportCount(0)
                    .countReply(0)
                    .isLocked(Boolean.FALSE)
                    .postId(post)
                    .memberId(memberList.get(new Random().nextInt(3)))
                    .build();

            commentList.add(comment);
        }
        return commentList;
    }

    private static PostDto.CreatePost setBackgroundPicPostDto() {
        return PostDto.CreatePost.builder()
                .content("테스트입니다.")
                .forMe(false)
                .location("35.123,12.454")
                .backgroundImgName("1")
                .hashTagName(List.of("테스트", "입니다"))
                .anonymity(false)
                .font("Jua")
                .fontColor("black")
                .fontSize(40)
                .fontBold(400)
                .build();
    }

    private static PostDto.CreatePost setUserUploadPicPostDto() throws IOException {
        MultipartFile file = new MockMultipartFile("1", "1.jpg", "image/jpeg", new FileInputStream("/Users/hw0603/DoranDoranPic/BackgroundPic/2.jpg"));
        return PostDto.CreatePost.builder()
                .content("테스트입니다.")
                .forMe(false)
                .location("35.123,12.454")
                .backgroundImgName("")
                .file(file)
                .hashTagName(List.of("테스트", "입니다"))
                .anonymity(false)
                .font("Jua")
                .fontColor("black")
                .fontSize(40)
                .fontBold(400)
                .build();
    }
    private static Member setMember1() {
        return Member.builder()
                .email("9643us@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000, 12, 12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();
    }

    private static Member setMember2() {
        return Member.builder()
                .email("thrusum@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000, 12, 12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();
    }

    private static Member setMember3() {
        return Member.builder()
                .email("jw1010110@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000, 12, 12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();
    }

    private static Point setLocation() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(37.31681241549904, 127.08951837477541);
        Point point = geometryFactory.createPoint(coordinate);
        return point;
    }

    private static Post setPost(Point point, Member member) {
        return Post.builder().content("테스트입니다.")
                .forMe(false)
                .location(point)
                .memberId(member)
                .switchPic(ImgType.DefaultBackground)
                .ImgName(1 + ".jpg")
                .font("Jua")
                .fontColor("black")
                .fontSize(20)
                .fontBold(400)
                .reportCount(0)
                .anonymity(false)
                .isLocked(false)
                .build();
    }

    private static PostLike setPostLike(Post post, Boolean checkDelete) {
        return PostLike.builder()
                .postId(post)
                .memberId(setMember1())
                .checkDelete(checkDelete)
                .build();
    }
}