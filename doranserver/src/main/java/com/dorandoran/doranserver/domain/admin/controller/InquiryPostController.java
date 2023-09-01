package com.dorandoran.doranserver.domain.admin.controller;

import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.admin.domain.InquiryComment;
import com.dorandoran.doranserver.domain.admin.domain.InquiryPost;
import com.dorandoran.doranserver.domain.admin.dto.InquiryDto;
import com.dorandoran.doranserver.domain.admin.service.InquiryCommentService;
import com.dorandoran.doranserver.domain.admin.service.InquiryPostService;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Timed
@RestController
@RequiredArgsConstructor
@Slf4j
public class InquiryPostController {
    private final MemberService memberService;
    private final InquiryPostService inquiryPostService;
    private final InquiryCommentService inquiryCommentService;

    @GetMapping("/admin/inquiryPost/board")
    public ResponseEntity<List<InquiryDto.ReadInquiryPostBoard>> getAllInquiryPost(@RequestParam("page") Integer page){
        List<InquiryPost> inquiryPostPage = inquiryPostService.findAll(page);
        return ResponseEntity.ok().body(makeInquiryPostBoardList(inquiryPostPage));
    }

    @GetMapping("/admin/inquiryPost/{inquiryPostId}")
    public ResponseEntity<InquiryDto.ReadInquiryPost> getInquiryPostDetail(@PathVariable Long inquiryPostId){
        InquiryPost inquiryPost = inquiryPostService.findInquiryPost(inquiryPostId);
        List<InquiryComment> inquiryComments = inquiryCommentService.findCommentByPost(inquiryPost);
        List<InquiryDto.ReadInquiryComment> inquiryCommentListDto = inquiryComments.stream()
                .map((inquiryComment -> InquiryDto.ReadInquiryComment.builder()
                        .inquiryComment(inquiryComment)
                        .build()))
                .toList();

        InquiryDto.ReadInquiryPost readInquiryPost = InquiryDto.ReadInquiryPost.builder()
                .inquiryPost(inquiryPost)
                .inquiryCommentList(inquiryCommentListDto)
                .build();
        return ResponseEntity.ok().body(readInquiryPost);
    }

    @Trace
    @PostMapping("/api/inquiryPost")
    public ResponseEntity<?> saveInquiryPost(@RequestBody InquiryDto.CreateInquiryPost createInquiryPost,
                                @AuthenticationPrincipal UserDetails userDetails){
        Member member = memberService.findByEmail(userDetails.getUsername());
        InquiryPost inquiryPost = InquiryPost.builder()
                .title(createInquiryPost.getTitle())
                .content(createInquiryPost.getContent())
                .memberId(member)
                .build();
        inquiryPostService.saveInquiryPost(inquiryPost);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/inquiryPost/{inquiryPostId}")
    public ResponseEntity<?> deleteInquiryPost(@PathVariable Long inquiryPostId){
        InquiryPost inquiryPost = inquiryPostService.findInquiryPost(inquiryPostId);

        List<InquiryComment> inquiryCommentList = inquiryCommentService.findCommentByPost(inquiryPost);
        inquiryCommentService.deleteInquiryCommentList(inquiryCommentList);

        inquiryPostService.deleteInquiryPost(inquiryPost);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/inquiryPost/search")
    public ResponseEntity<List<InquiryDto.ReadInquiryPostBoard>> searchInquiryPost(@RequestParam("page") Integer page,
                                                                                   @RequestParam("searchType") String searchType,
                                                                                   @RequestParam("content") String content){
        List<InquiryPost> inquiryPostList =
                switch (searchType) {
                    case "title" -> inquiryPostService.findByPostTitle(page, content);
                    case "content" -> inquiryPostService.findByPostContent(page, content);
                    case "postTime" -> inquiryPostService.findByPostTime(page, content);
                    case "answerType" -> inquiryPostService.findByAnswerType(page, content);
                    default -> throw new IllegalArgumentException("해당 검색 조건은 없습니다.");
                };
        return ResponseEntity.ok().body(makeInquiryPostBoardList(inquiryPostList));
    }

    private static List<InquiryDto.ReadInquiryPostBoard> makeInquiryPostBoardList(List<InquiryPost> inquiryPostPage) {
        return inquiryPostPage.stream()
                .map(inquiryPost -> InquiryDto.ReadInquiryPostBoard.builder()
                        .inquiryPost(inquiryPost)
                        .build())
                .toList();
    }
}