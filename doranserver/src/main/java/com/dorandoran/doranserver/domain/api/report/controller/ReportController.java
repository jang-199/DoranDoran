package com.dorandoran.doranserver.domain.api.report.controller;

import com.dorandoran.doranserver.domain.api.post.service.PostService;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.comment.service.CommentService;
import com.dorandoran.doranserver.domain.api.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.api.member.domain.LockMember;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.report.domain.ReportComment;
import com.dorandoran.doranserver.domain.api.report.domain.ReportPost;
import com.dorandoran.doranserver.domain.api.report.domain.ReportReply;
import com.dorandoran.doranserver.domain.api.report.dto.ReportDto;
import com.dorandoran.doranserver.domain.api.report.service.ReportCommentService;
import com.dorandoran.doranserver.domain.api.report.service.ReportPostService;
import com.dorandoran.doranserver.domain.api.report.service.ReportReplyService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@Timed
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {
    private final MemberService memberService;
    private final PostService postService;
    private final ReportPostService reportPostService;
    private final ReportCommentService reportCommentService;
    private final ReportReplyService reportReplyService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final LockMemberService lockMemberService;

    @Trace
    @PostMapping("/post/report")
    public ResponseEntity<?> saveReportPost(@RequestBody ReportDto.CreateReportPost reportPostRequestDto,
                                            @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Post post = postService.findSinglePost(reportPostRequestDto.getPostId());

        if (reportPostService.existReportPost(post,member)){
            log.info("이미 신고한 회원입니다.");
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            ReportPost reportPost = new ReportPost(post, member, reportPostRequestDto.getReportContent());
            reportPostService.saveReportPost(reportPost);
            reportPostService.postBlockLogic(post);
            log.info("{}님이 {}번 글에 신고를 했습니다.",userEmail, post.getPostId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Trace
    @PostMapping("/comment/report")
    public ResponseEntity<?> saveReportComment(@RequestBody ReportDto.CreateReportComment
                                                           reportCommentRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Comment comment = commentService.findCommentByCommentId(reportCommentRequestDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (reportCommentService.existedReportComment(comment, member)) {
            log.info("이미 신고한 회원입니다.");
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            ReportComment reportComment = new ReportComment(comment, member, reportCommentRequestDto.getReportContent());
            reportCommentService.saveReportComment(reportComment);
            reportCommentService.commentBlockLogic(comment);
            log.info("{}님이 {}번 댓글에 신고를 했습니다.",userEmail,comment.getCommentId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Trace
    @PostMapping("/reply/report")
    public ResponseEntity<?> saveReportReply(@RequestBody ReportDto.CreateReportReply reportReplyRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Reply reply = replyService.findReplyByReplyId(reportReplyRequestDto.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (reportReplyService.existedReportReply(reply, member)){
            log.info("이미 신고한 회원입니다.");
            return new ResponseEntity<>("이미 신고한 회원입니다.", HttpStatus.BAD_REQUEST);
        }else {
            ReportReply reportReply = new ReportReply(reply, member, reportReplyRequestDto.getReportContent());
            reportReplyService.saveReportReply(reportReply);
            reportReplyService.replyBlockLogic(reply);
            log.info("{}님이 {}번 대댓글에 신고를 했습니다.", userEmail, reply.getReplyId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Trace
    @GetMapping("/lockMember")
    public ResponseEntity<?> searchLockMember(@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            return new ResponseEntity<>("해당 계정은 현재 정지되었습니다.",HttpStatus.BAD_REQUEST);
        }else {
            return new ResponseEntity<>("해당 사용자는 활성화 상태입니다.",HttpStatus.OK);
        }
    }
}
