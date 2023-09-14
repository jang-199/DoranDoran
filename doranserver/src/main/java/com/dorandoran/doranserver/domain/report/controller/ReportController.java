package com.dorandoran.doranserver.domain.report.controller;

import com.dorandoran.doranserver.domain.report.domain.ReportType.ReportType;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.domain.report.domain.ReportComment;
import com.dorandoran.doranserver.domain.report.domain.ReportPost;
import com.dorandoran.doranserver.domain.report.domain.ReportReply;
import com.dorandoran.doranserver.domain.report.dto.ReportDto;
import com.dorandoran.doranserver.domain.report.service.ReportCommentService;
import com.dorandoran.doranserver.domain.report.service.ReportPostService;
import com.dorandoran.doranserver.domain.report.service.ReportReplyService;
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
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            ReportType reportType = ReportType.getReportType(reportPostRequestDto.getReportContent());

            ReportPost reportPost = new ReportPost(post, member, reportType, reportPostRequestDto.getReportContent());
            reportPostService.saveReportPost(reportPost);
            reportPostService.postBlockLogic(post);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Trace
    @PostMapping("/comment/report")
    public ResponseEntity<?> saveReportComment(@RequestBody ReportDto.CreateReportComment
                                                           reportCommentRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Comment comment = commentService.findCommentByCommentId(reportCommentRequestDto.getCommentId());

        if (reportCommentService.existedReportComment(comment, member)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            ReportType reportType = ReportType.getReportType(reportCommentRequestDto.getReportContent());

            ReportComment reportComment = new ReportComment(comment, member, reportType, reportCommentRequestDto.getReportContent());
            reportCommentService.saveReportComment(reportComment);
            reportCommentService.commentBlockLogic(comment);
            return ResponseEntity.status(HttpStatus.CREATED).build();
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
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }else {
            ReportType reportType = ReportType.getReportType(reportReplyRequestDto.getReportContent());

            ReportReply reportReply = new ReportReply(reply, member, reportType, reportReplyRequestDto.getReportContent());
            reportReplyService.saveReportReply(reportReply);
            reportReplyService.replyBlockLogic(reply);
            return ResponseEntity.status(HttpStatus.CREATED).build();
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
