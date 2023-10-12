package com.dorandoran.doranserver.domain.report.controller;

import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.domain.lockType.LockType;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.domain.report.dto.ReportDto;
import com.dorandoran.doranserver.domain.report.service.ReportCommentService;
import com.dorandoran.doranserver.domain.report.service.ReportPostService;
import com.dorandoran.doranserver.domain.report.service.ReportReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String REFRESH_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E";
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @MockBean
    private PostService postService;
    @MockBean
    private ReportPostService reportPostService;
    @MockBean
    private ReportCommentService reportCommentService;
    @MockBean
    private ReportReplyService reportReplyService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ReplyService replyService;
    @MockBean
    private LockMemberService lockMemberService;
    @MockBean
    private FirebaseService firebaseService;

    @Test
    void saveReportPost() throws Exception {
        //given
        Member member = setMember();
        Post post = setPost(null, member);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(postService.findSinglePost(BDDMockito.any())).willReturn(post);
        BDDMockito.given(reportPostService.existReportPost(BDDMockito.any(),BDDMockito.any())).willReturn(Boolean.TRUE).willReturn(Boolean.FALSE);
        BDDMockito.doNothing().when(reportPostService).saveReportPost(BDDMockito.any());
        BDDMockito.doNothing().when(reportPostService).postBlockLogic(BDDMockito.any());

        ReportDto.CreateReportPost request = ReportDto.CreateReportPost.builder()
                .postId(post.getPostId())
                .reportContent("선정성")
                .build();

        String content = new ObjectMapper().writeValueAsString(request);

        //when
        ResultActions failResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/report")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions successResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/post/report")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        failResultActions.andExpect(MockMvcResultMatchers.status().isConflict());
        successResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void saveReportComment() throws Exception {
        //given
        Member member = setMember();
        Post post = setPost(null, member);
        Comment comment = setComment(post, member);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(commentService.findCommentByCommentId(BDDMockito.any())).willReturn(comment);
        BDDMockito.given(reportCommentService.existedReportComment(BDDMockito.any(),BDDMockito.any())).willReturn(Boolean.TRUE).willReturn(Boolean.FALSE);
        BDDMockito.doNothing().when(reportCommentService).saveReportComment(BDDMockito.any());
        BDDMockito.doNothing().when(reportCommentService).commentBlockLogic(BDDMockito.any());

        ReportDto.CreateReportComment request = ReportDto.CreateReportComment.builder()
                .commentId(comment.getCommentId())
                .reportContent("선정성")
                .build();

        String content = new ObjectMapper().writeValueAsString(request);

        //when
        ResultActions failResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/comment/report")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions successResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/comment/report")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        failResultActions.andExpect(MockMvcResultMatchers.status().isConflict());
        successResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void saveReportReply() throws Exception {
        //given
        Member member = setMember();
        Post post = setPost(null, member);
        Comment comment = setComment(post, member);
        Reply reply = setReply(comment, member);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(replyService.findReplyByReplyId(BDDMockito.any())).willReturn(reply);
        BDDMockito.given(reportReplyService.existedReportReply(BDDMockito.any(),BDDMockito.any())).willReturn(Boolean.TRUE).willReturn(Boolean.FALSE);
        BDDMockito.doNothing().when(reportReplyService).saveReportReply(BDDMockito.any());
        BDDMockito.doNothing().when(reportReplyService).replyBlockLogic(BDDMockito.any());

        ReportDto.CreateReportReply request = ReportDto.CreateReportReply.builder()
                .replyId(reply.getReplyId())
                .reportContent("선정성")
                .build();

        String content = new ObjectMapper().writeValueAsString(request);

        //when
        ResultActions failResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/reply/report")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions successResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/reply/report")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        failResultActions.andExpect(MockMvcResultMatchers.status().isConflict());
        successResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void searchLockMember() throws Exception {
        //given
        Member member = setMember();
        LockMember lockMember = new LockMember(member, Duration.ofDays(1), LockType.Day1);
        BDDMockito.given(memberService.findByEmail(BDDMockito.any())).willReturn(member);
        BDDMockito.given(lockMemberService.findLockMember(BDDMockito.any())).willReturn(Optional.of(lockMember)).willReturn(Optional.empty());

        //when
        ResultActions forbiddenResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/lockMember")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        ResultActions activeResultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/lockMember")
                        .header(HEADER_AUTHORIZATION, REFRESH_TOKEN)
        );

        //then
        forbiddenResultActions.andExpect(MockMvcResultMatchers.status().isForbidden());
        activeResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private static Member setMember() {
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

    private static Comment setComment(Post post, Member member){
        return Comment.builder()
                .postId(post)
                .memberId(member)
                .comment("테스트 댓글입니다")
                .reportCount(0)
                .countReply(0)
                .anonymity(false)
                .checkDelete(false)
                .isLocked(false)
                .secretMode(false)
                .build();
    }

    private static Reply setReply(Comment comment, Member member){
        return Reply.builder()
                .commentId(comment)
                .memberId(member)
                .reply("테스트 대댓글입니다.")
                .isLocked(false)
                .checkDelete(false)
                .secretMode(false)
                .anonymity(false)
                .reportCount(0)
                .build();
    }
}