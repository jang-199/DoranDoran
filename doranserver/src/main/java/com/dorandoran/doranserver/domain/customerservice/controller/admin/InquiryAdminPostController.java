package com.dorandoran.doranserver.domain.customerservice.controller.admin;

import com.dorandoran.doranserver.domain.customerservice.annotation.Admin;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryComment;
import com.dorandoran.doranserver.domain.customerservice.domain.InquiryPost;
import com.dorandoran.doranserver.domain.customerservice.dto.InquiryDto;
import com.dorandoran.doranserver.domain.customerservice.service.InquiryCommentService;
import com.dorandoran.doranserver.domain.customerservice.service.InquiryPostService;
import com.dorandoran.doranserver.global.util.InquiryResponseUtils;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Timed
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class InquiryAdminPostController {
    private final InquiryPostService inquiryPostService;
    private final InquiryCommentService inquiryCommentService;

    @Admin
    @GetMapping("/inquiryPost/board")
    public ResponseEntity<List<InquiryDto.ReadInquiryPostBoard>> getAllInquiryPost(@RequestParam("page") Integer page){
        List<InquiryPost> inquiryPostPage = inquiryPostService.findAll(page);
        return ResponseEntity.ok().body(InquiryResponseUtils.makeInquiryPostList(inquiryPostPage));
    }

    @Admin
    @GetMapping("/inquiryPost/{inquiryPostId}")
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

    @Admin
    @DeleteMapping("/inquiryPost/{inquiryPostId}")
    public ResponseEntity<?> deleteInquiryPost(@PathVariable Long inquiryPostId){
        InquiryPost inquiryPost = inquiryPostService.findInquiryPost(inquiryPostId);

        List<InquiryComment> inquiryCommentList = inquiryCommentService.findCommentByPost(inquiryPost);
        inquiryCommentService.deleteInquiryCommentList(inquiryCommentList);

        inquiryPostService.deleteInquiryPost(inquiryPost);
        return ResponseEntity.ok().build();
    }

    @Admin
    @GetMapping("/inquiryPost/search")
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
        return ResponseEntity.ok().body(InquiryResponseUtils.makeInquiryPostList(inquiryPostList));
    }
}
