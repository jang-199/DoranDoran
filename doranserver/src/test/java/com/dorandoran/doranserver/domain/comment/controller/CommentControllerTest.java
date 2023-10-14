package com.dorandoran.doranserver.domain.comment.controller;

import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.comment.service.common.CommentCommonService;
import com.dorandoran.doranserver.domain.comment.service.common.ReplyCommonServiceImpl;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.lockType.LockType;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.service.AnonymityMemberService;
import com.dorandoran.doranserver.domain.post.service.PopularPostService;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String REFRESH_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @MockBean
    private PostService postService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentLikeService commentLikeService;
    @MockBean
    private ReplyService replyService;
    @MockBean
    private PopularPostService popularPostService;
    @MockBean
    private AnonymityMemberService anonymityMemberService;
    @MockBean
    private LockMemberService lockMemberService;
    @MockBean
    private MemberBlockListService memberBlockListService;
    @MockBean
    private FirebaseService firebaseService;
    @MockBean
    private CommentCommonService commentCommonService;
    @MockBean
    private ReplyCommonServiceImpl replyCommonService;

    @Value("${doran.ip.address}")
    String ipAddress;
    @Test
    void inquiryComment() throws Exception{
        //given
        Member postMember = setMember1();
        Member blockMember = setMember2();
        Member requestMember = setMember3();
        List<Member> blockMemberList = List.of(blockMember);
        Post post = setPost(setLocation(), postMember);
        List<Comment> commentList = setCommentList(requestMember, post);
        List<Reply> replyList = setReplyList(requestMember, commentList);
        HashMap<Long, Long> commentLikeCnt = setCommentLikeCnt(commentList);
        HashMap<Long, Boolean> commentLikeResult = setCommentLikeResult(commentList);

        BDDMockito.given(postService.findSinglePost(BDDMockito.any())).willReturn(post);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(postMember);
        BDDMockito.given(memberBlockListService.findMemberBlockListByBlockingMember(BDDMockito.any())).willReturn(new ArrayList<>()).willReturn(blockMemberList);
        BDDMockito.given(anonymityMemberService.findAllUserEmail(BDDMockito.any())).willReturn(new ArrayList<>());
        BDDMockito.given(commentService.findNextComments(BDDMockito.any(), BDDMockito.any())).willReturn(commentList);
        BDDMockito.given(commentService.checkExistAndDelete(BDDMockito.any())).willReturn(Boolean.TRUE);
        BDDMockito.given(replyService.findRankRepliesByComments(BDDMockito.any())).willReturn(replyList);
        BDDMockito.given(commentLikeService.findCommentLikeCnt(BDDMockito.any())).willReturn(commentLikeCnt);
        BDDMockito.given(commentLikeService.findCommentLikeResult(BDDMockito.any(),BDDMockito.any())).willReturn(commentLikeResult);

        BDDMockito.given(replyService.checkExistAndDelete(BDDMockito.any())).willReturn(Boolean.FALSE);
        BDDMockito.doNothing().when(replyService).checkSecretReply(BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any());
        BDDMockito.doNothing().when(replyService).checkReplyAnonymityMember(BDDMockito.any(),BDDMockito.any(),BDDMockito.any());
        BDDMockito.doNothing().when(commentService).checkSecretComment(BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any());
        BDDMockito.doNothing().when(commentService).checkCommentAnonymityMember(BDDMockito.any(),BDDMockito.any(),BDDMockito.any());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/comment")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .param("postId", "1")
                        .param("commentId", "1")
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].['isExistNextComment']").value(true));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].['commentData'].[0].['replies'].['isExistNextReply']").value(false));
    }

    @Test
    void saveComment() throws Exception{
        //given
        Member member = setMember1();
        LockMember lockMember = new LockMember(member, Duration.ofDays(1), LockType.Day1);
        Post post = setPost(setLocation(), member);
        CommentDto.CreateComment commentCreateDto = CommentDto.CreateComment.builder()
                .postId(1L)
                .comment("테스트입니다")
                .secretMode(Boolean.FALSE)
                .anonymity(Boolean.FALSE)
                .build();
        String content = new ObjectMapper().writeValueAsString(commentCreateDto);

        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(lockMemberService.findLockMember(BDDMockito.any())).willReturn(Optional.of(lockMember)).willReturn(Optional.empty());
        BDDMockito.given(lockMemberService.checkCurrentLocked(BDDMockito.any())).willReturn(Boolean.TRUE).willReturn(Boolean.FALSE);
        BDDMockito.doNothing().when(lockMemberService).deleteLockMember(BDDMockito.any());
        BDDMockito.given(postService.findSinglePost(BDDMockito.any())).willReturn(post);
        BDDMockito.given(anonymityMemberService.findAllUserEmail(BDDMockito.any())).willReturn(new ArrayList<>());
        BDDMockito.given(commentService.findCommentByPost(BDDMockito.any())).willReturn(null);
        BDDMockito.given(popularPostService.findPopularPostByPost(BDDMockito.any())).willReturn(null);
        BDDMockito.doNothing().when(commentCommonService).saveComment(BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any());
        BDDMockito.doNothing().when(firebaseService).notifyComment(BDDMockito.any(),BDDMockito.any());

        //when
        ResultActions failResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/comment")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions successResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/comment")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        failResultActions.andExpect(MockMvcResultMatchers.status().isForbidden());
        successResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void deleteComment() throws Exception{
        //given
        Member commentMember = setMember1();
        Member requestMember = setMember2();
        Post post = setPost(setLocation(), commentMember);
        Comment canDeleteComment = setComment(commentMember, post, 1L);
        Comment cannotDeleteComment = setComment(requestMember, post, 1L);
        CommentDto.DeleteComment commentDto = CommentDto.DeleteComment.builder().commentId(1L).build();
        String content = new ObjectMapper().writeValueAsString(commentDto);
        BDDMockito.given(commentService.findCommentByCommentId(BDDMockito.any())).willReturn(canDeleteComment).willReturn(cannotDeleteComment);
        BDDMockito.doNothing().when(commentService).setCheckDelete(BDDMockito.any());

        //when
        ResultActions successDeleteResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/comment")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions failDeleteResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/comment")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        successDeleteResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        failDeleteResultActions.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void commentLike() throws Exception{
        //given
        Member commentMember = setMember1();
        Member requestMember = setMember2();
        Post post = setPost(setLocation(), commentMember);
        Comment canLikecomment = setComment(requestMember, post, 1L);
        Comment cannotLikecomment = setComment(commentMember, post, 1L);
        CommentLike commentLike = CommentLike.builder().commentId(canLikecomment).memberId(requestMember).checkDelete(Boolean.FALSE).build();
        CommentDto.LikeComment likeCommentDto = CommentDto.LikeComment.builder().commentId(1L).postId(1L).build();
        String content = new ObjectMapper().writeValueAsString(likeCommentDto);

        BDDMockito.given(commentService.findCommentByCommentId(BDDMockito.any())).willReturn(canLikecomment).willReturn(cannotLikecomment);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(commentMember);
        BDDMockito.given(commentLikeService.findCommentLikeOne(BDDMockito.any(), BDDMockito.any())).willReturn(Optional.of(commentLike));
        BDDMockito.doNothing().when(commentLikeService).checkCommentLike(BDDMockito.any(), BDDMockito.any(), BDDMockito.any(), BDDMockito.any(), BDDMockito.any());
        BDDMockito.doNothing().when(firebaseService).notifyCommentLike(BDDMockito.any(), BDDMockito.any());

        //when
        ResultActions successLikeresultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/comment/like")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions failLikeresultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/comment/like")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        successLikeresultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        failLikeresultActions.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void inquiryReply() throws Exception{
        //given
        Member postMember = setMember1();
        Member blockMember = setMember2();
        Member requestMember = setMember3();
        List<Member> blockMemberList = List.of(blockMember);
        Post post = setPost(setLocation(), postMember);
        List<Comment> commentList = setCommentList(requestMember, post);
        List<Reply> replyList = setReplyList(requestMember, commentList);
        BDDMockito.given(postService.findSinglePost(BDDMockito.any())).willReturn(post);
        BDDMockito.given(anonymityMemberService.findAllUserEmail(BDDMockito.any())).willReturn(null);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(requestMember);
        BDDMockito.given(memberBlockListService.findMemberBlockListByBlockingMember(BDDMockito.any())).willReturn(blockMemberList);
        BDDMockito.given(replyService.findNextReplies(BDDMockito.any(), BDDMockito.any())).willReturn(replyList);
        BDDMockito.given(replyService.checkExistAndDelete(BDDMockito.any())).willReturn(Boolean.TRUE);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/reply")
                        .param("postId", "1")
                        .param("commentId", "1")
                        .param("replyId", "1")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].['isExistNextReply']").value(true));
    }

    @Test
    void saveReply() throws Exception{
        //when
        Member member = setMember1();
        Post post = setPost(setLocation(), member);
        Comment comment = setComment(member, post, 1L);
        LockMember lockMember = new LockMember(member, Duration.ofDays(1), LockType.Day1);
        ReplyDto.CreateReply createReplyDto = ReplyDto.CreateReply.builder().reply("테스트").commentId(1L).anonymity(Boolean.FALSE).secretMode(Boolean.FALSE).build();
        String content = new ObjectMapper().writeValueAsString(createReplyDto);
        ArrayList<Member> members = new ArrayList<>();
        members.add(member);

        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(commentService.findCommentByCommentId(BDDMockito.any())).willReturn(comment);
        BDDMockito.given(lockMemberService.findLockMember(BDDMockito.any())).willReturn(Optional.of(lockMember));
        BDDMockito.given(lockMemberService.checkCurrentLocked(BDDMockito.any())).willReturn(Boolean.TRUE).willReturn(Boolean.FALSE);
        BDDMockito.doNothing().when(lockMemberService).deleteLockMember(BDDMockito.any());
        BDDMockito.given(anonymityMemberService.findAllUserEmail(BDDMockito.any())).willReturn(new ArrayList<>());
        BDDMockito.doNothing().when(replyCommonService).saveReply(BDDMockito.any(), BDDMockito.any(), BDDMockito.any(), BDDMockito.any(), BDDMockito.any(), BDDMockito.any());
        BDDMockito.given(replyService.findReplyMemberByComment(BDDMockito.any())).willReturn(members);

        //given
        ResultActions failResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/reply")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions successResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/reply")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        failResultActions.andExpect(MockMvcResultMatchers.status().isForbidden());
        successResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void replyDelete() throws Exception{
        //given
        Member replyMember = setMember1();
        Member requestMember = setMember2();
        Post post = setPost(setLocation(), replyMember);
        Comment comment = setComment(replyMember, post, 1L);
        Reply canDeleteReply = setReply(replyMember, comment, 1L);
        Reply cannotDeleteReply = setReply(requestMember, comment, 1L);
        BDDMockito.given(replyService.findReplyByReplyId(BDDMockito.any())).willReturn(canDeleteReply).willReturn(cannotDeleteReply);
        BDDMockito.doNothing().when(replyService).setCheckDelete(BDDMockito.any());
        ReplyDto.DeleteReply deleteReplyDto = ReplyDto.DeleteReply.builder().replyId(1L).build();
        String content = new ObjectMapper().writeValueAsString(deleteReplyDto);

        //when
        ResultActions successResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/reply")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions failResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/reply")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //given
        successResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        failResultActions.andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    private List<Reply> setReplyList(Member member, List<Comment> commentList) {
        ArrayList<Reply> replyList = new ArrayList<>();

        for (Comment comment : commentList) {
            for (long i = 0L; i<10; i++) {
                Reply reply = setReply(member, comment, i);

                replyList.add(reply);
            }

        }
        return replyList;
    }

    private static Reply setReply(Member member, Comment comment, long i) {
        return Reply.builder()
                .replyId(i)
                .reply("테스트 대댓글입니다.")
                .commentId(comment)
                .memberId(member)
                .anonymity(Boolean.FALSE)
                .secretMode(Boolean.FALSE)
                .checkDelete(Boolean.FALSE)
                .isLocked(Boolean.FALSE)
                .reportCount(0)
                .build();
    }

    private List<Comment> setCommentList(Member member, Post post) {
        ArrayList<Comment> commentList = new ArrayList<>();

        for (long i = 0; i<10; i++){
            Comment comment = setComment(member, post, i);

            commentList.add(comment);
        }
        return commentList;
    }

    private static Comment setComment(Member member, Post post, long i) {
        return Comment.builder()
                .commentId(i)
                .comment("테스트 댓글입니다.")
                .secretMode(Boolean.FALSE)
                .checkDelete(Boolean.FALSE)
                .anonymity(Boolean.FALSE)
                .reportCount(0)
                .countReply(0)
                .isLocked(Boolean.FALSE)
                .postId(post)
                .memberId(member)
                .build();
    }

    private static HashMap<Long, Long> setCommentLikeCnt(List<Comment> commentList){
        HashMap<Long, Long> commntLikeHashMap = new HashMap<>();
        List<Long> comments = commentList.stream().map(Comment::getCommentId).toList();

        for (Long commentId : comments) {
            commntLikeHashMap.put(commentId, commentId);
        }

        return commntLikeHashMap;
    }

    private static HashMap<Long, Boolean> setCommentLikeResult(List<Comment> commentList){
        HashMap<Long, Boolean> commntLikeResultHashMap = new HashMap<>();
        List<Long> comments = commentList.stream().map(Comment::getCommentId).toList();

        for (Long commentId : comments) {
            commntLikeResultHashMap.put(commentId, Boolean.FALSE);
        }

        return commntLikeResultHashMap;
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

}