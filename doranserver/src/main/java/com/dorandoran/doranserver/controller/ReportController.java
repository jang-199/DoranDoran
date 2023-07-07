package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.ReportCommentRequestDto;
import com.dorandoran.doranserver.dto.ReportReplyRequestDto;
import com.dorandoran.doranserver.dto.ReportRequestDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {
    private final MemberService memberService;
    private final PostService postService;
    private final ReportService reportService;
    private final CommentService commentService;
    private final ReplyService replyService;

    @PostMapping("/post/report")
    @Transactional
    public ResponseEntity<?> saveReportPost(@RequestBody ReportRequestDto reportRequestDto,
                                            @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Post post = postService.findSinglePost(reportRequestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        ReportPost reportPost = new ReportPost(post, member, reportRequestDto.getContent());

        if (reportService.existReportPost(post,member)){
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            reportService.saveReportPost(reportPost);
            post.setReportCount(post.getReportCount()+1);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/comment/report")
    public ResponseEntity<?> saveReportComment(@RequestBody ReportCommentRequestDto reportCommentRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Comment comment = commentService.findCommentByCommentId(reportCommentRequestDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        ReportComment reportComment = new ReportComment(comment, member, reportCommentRequestDto.getReportContent());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reply/report")
    public ResponseEntity<?> saveReportReply(@RequestBody ReportReplyRequestDto reportReplyRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Reply reply = replyService.findReplyByReplyId(reportReplyRequestDto.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        ReportReply reportReply = new ReportReply(reply, member, reportReplyRequestDto.getReportContent());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
