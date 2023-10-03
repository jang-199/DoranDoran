package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.hashtag.service.HashTagService;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class PostControllerTest {
    @Mock
    private MemberService memberService;
    @Mock
    private PostLikeService postLikeService;
    @Mock
    private HashTagService hashTagService;
    @Mock
    private PostService postService;
    @Mock
    private PostHashService postHashService;
    @Mock
    private CommentService commentService;
    @Mock
    private CommentLikeService commentLikeService;
    @Mock
    private ReplyService replyService;
    @Mock
    private AnonymityMemberService anonymityMemberService;
    @Mock
    private LockMemberService lockMemberService;
    @Mock
    private PostCommonService postCommonService;
    @Mock
    private MemberBlockListService memberBlockListService;
    @Mock
    private FirebaseService firebaseService;
    @Mock
    private CommentResponseUtils commentResponseUtils;

    @Test
    void savePost() {
    }

    @Test
    void postDelete() {
    }

    @Test
    void postLike() {
    }

    @Test
    void postDetails() {
    }
}