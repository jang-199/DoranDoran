package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.blockMember.BlockMemberDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.service.*;
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

    @PostMapping("/block/member")
    ResponseEntity blockMember(@RequestBody BlockMemberDto blockMemberDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Member blockingMember = memberService.findByEmail(userDetails.getUsername());

        switch (blockMemberDto.getBlockType()) {
            case post -> {
                Member blockedMember = postService.findSinglePost(blockMemberDto.getId()).getMemberId();
                memberBlockListService.addBlockList(blockingMember,blockedMember);
                break;
            }
            case comment -> {
                Member blockedMember = commentService.findCommentByCommentId(blockMemberDto.getId()).orElseThrow().getMemberId();
                memberBlockListService.addBlockList(blockingMember,blockedMember);
                break;
            }
            case reply -> {
                Member blockedMember = replyService.findReplyByReplyId(blockMemberDto.getId()).orElseThrow().getMemberId();
                memberBlockListService.addBlockList(blockingMember,blockedMember);
                break;
            }
        }
        return ResponseEntity.ok().build();

    }

}
