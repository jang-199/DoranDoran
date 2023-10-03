package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.service.AnonymityMemberService;
import com.dorandoran.doranserver.domain.post.service.PostHashService;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.domain.post.service.common.PostCommonService;
import com.dorandoran.doranserver.global.util.CommentResponseUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class PostControllerTest {
    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;
    @Value("${doran.ip.address}")
    String ipAddress;
    @MockBean
    private MemberService memberService;
    @MockBean
    private PostLikeService postLikeService;
    @MockBean
    private HashTagService hashTagService;
    @MockBean
    private PostService postService;
    @MockBean
    private PostHashService postHashService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentLikeService commentLikeService;
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
    @MockBean
    private FirebaseService firebaseService;
    @MockBean
    private CommentResponseUtils commentResponseUtils;

    @Test
    void savePost() throws Exception {
        //given
        Member member = Member.builder()
                .email("9643us@naver.com")
                .dateOfBirth(LocalDate.now())
                .firebaseToken("firebasetoken")
                .closureDate(LocalDate.of(2000,12,12))
                .nickname("doran")
                .checkNotification(Boolean.TRUE)
                .signUpDate(LocalDateTime.now())
                .refreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqdzEwMTAxMTBAZ21haWwuY29tIiwiaWF0IjoxNjkxMjYwMjkzLCJleHAiOjE3MDY4MTIyOTMsInN1YiI6IuyImOyduCIsIlJPTEUiOiJST0xFX1VTRVIiLCJlbWFpbCI6Ijk2NDN1c0BuYXZlci5jb20ifQ.Jp88iBJy6OEfLyBGu8bQ9q8yAiQXi_M50syJJ5aTR0E")
                .osType(OsType.Ios)
                .build();




        //when

        //then
    }

    @Test
    void postDelete() throws Exception {
    }

    @Test
    void postLike() throws Exception {
    }

    @Test
    void postDetails() throws Exception {
    }
}