package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.ReportCommentRequestDto;
import com.dorandoran.doranserver.dto.ReportReplyRequestDto;
import com.dorandoran.doranserver.dto.ReportPostRequestDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.service.*;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.ReportPost;
import com.dorandoran.doranserver.service.MemberService;
import com.dorandoran.doranserver.service.PostService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Timed
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Transactional
public class ReportController {
    private final MemberService memberService;
    private final PostService postService;
    private final ReportPostService reportPostService;
    private final ReportCommentService reportCommentService;
    private final ReportReplyService reportReplyService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final LockMemberService lockMemberService;

    @PostMapping("/post/report")
    public ResponseEntity<?> saveReportPost(@RequestBody ReportPostRequestDto reportPostRequestDto,
                                            @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Post post = postService.findSinglePost(reportPostRequestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if (reportPostService.existReportPost(post,member)){
            log.info("이미 신고한 회원입니다.");
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            ReportPost reportPost = new ReportPost(post, member, reportPostRequestDto.getReportContent());
            reportPostService.saveReportPost(reportPost);
            post.setReportCount(post.getReportCount()+1);
            log.info("{}님이 {}번 글에 신고를 했습니다.",userEmail, post.getPostId());
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

        if (reportCommentService.existedReportComment(comment, member)) {
            log.info("이미 신고한 회원입니다.");
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            ReportComment reportComment = new ReportComment(comment, member, reportCommentRequestDto.getReportContent());
            reportCommentService.saveReportComment(reportComment);
            comment.setReportCount(comment.getReportCount()+1);
            if (comment.getReportCount() == 5 && comment.getIsLocked() == Boolean.FALSE){
                comment.setLocked();
                member.addTotalReportTime();
            }
            log.info("{}님이 {}번 댓글에 신고를 했습니다.",userEmail,comment.getCommentId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/reply/report")
    public ResponseEntity<?> saveReportReply(@RequestBody ReportReplyRequestDto reportReplyRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Reply reply = replyService.findReplyByReplyId(reportReplyRequestDto.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (reportReplyService.existedReportReply(reply, member)){
            log.info("이미 신고한 회원입니다.");
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            ReportReply reportReply = new ReportReply(reply, member, reportReplyRequestDto.getReportContent());
            reportReplyService.saveReportReply(reportReply);
            reply.setReportCount(reply.getReportCount()+1);
            log.info("{}님이 {}번 대댓글에 신고를 했습니다.", userEmail, reply.getReplyId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
