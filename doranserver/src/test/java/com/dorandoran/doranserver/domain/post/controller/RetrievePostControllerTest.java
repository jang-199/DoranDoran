package com.dorandoran.doranserver.domain.post.controller;

import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.repository.MemberRepository;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.member.service.MemberServiceImpl;
import com.dorandoran.doranserver.domain.post.service.PostLikeService;
import com.dorandoran.doranserver.domain.post.service.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrievePostControllerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Mock
    private PostService postService;
    @Mock
    private PostLikeService postLikeService;
    @Mock
    private CommentService commentService;
    @Mock
    private MemberBlockListService memberBlockListService;
    @Spy
    private MemberService memberService;


    @Test
    @DisplayName("Method: Get, EndPoint: /post")
    void retrievePostTest() {
        //given
        final String EMAIL = "test@gmail.com";
        Member member = Member.builder()
                .email(EMAIL)
                .build();

        when(memberService.findByEmail(Mockito.any())).thenReturn(member);

        //when
        Member byEmail = memberService.findByEmail("EMAIL");

        //then
        assertThat(member).isEqualTo(byEmail);

    }
}