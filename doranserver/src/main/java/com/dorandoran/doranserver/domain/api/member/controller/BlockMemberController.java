package com.dorandoran.doranserver.domain.api.member.controller;

import com.dorandoran.doranserver.domain.api.admin.dto.BlockDto;
import com.dorandoran.doranserver.domain.api.comment.service.CommentService;
import com.dorandoran.doranserver.domain.api.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.api.member.service.MemberService;
import com.dorandoran.doranserver.domain.api.post.service.PostService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Timed
@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlockMemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final MemberBlockListService memberBlockListService;


    @PostMapping("/member/block")
    ResponseEntity<?> blockMember(@RequestBody BlockDto.BlockMember blockMemberDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Member blockingMember = memberService.findByEmail(userDetails.getUsername());

        switch (blockMemberDto.getBlockType()) {
            case post -> {
                Member blockedMember = postService.findSinglePost(blockMemberDto.getId()).getMemberId();
                memberBlockListService.addBlockList(blockingMember,blockedMember);
            }
            case comment -> {
                Member blockedMember = commentService.findCommentByCommentId(blockMemberDto.getId()).orElseThrow().getMemberId();
                memberBlockListService.addBlockList(blockingMember,blockedMember);
            }
            case reply -> {
                Member blockedMember = replyService.findReplyByReplyId(blockMemberDto.getId()).orElseThrow().getMemberId();
                memberBlockListService.addBlockList(blockingMember,blockedMember);
            }
        }
        return ResponseEntity.ok().build();

    }
}
