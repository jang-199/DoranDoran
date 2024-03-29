package com.dorandoran.doranserver.domain.customerservice.controller.admin;

import com.dorandoran.doranserver.domain.customerservice.annotation.Admin;
import com.dorandoran.doranserver.domain.customerservice.dto.BlockDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.global.util.exception.customexception.reply.NotFoundReplyException;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Timed
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class ManageBlockMemberController {
    public final PostService postService;
    public final CommentService commentService;
    public final ReplyService replyService;
    public final MemberService memberService;
    public final LockMemberService lockMemberService;

    @Admin
    @GetMapping("/post/block/{page}")
    public ResponseEntity<List<BlockDto.BlockPostResponse>> getBlockedPostList(@PathVariable Integer page){
        List<Post> blockedPost = postService.findBlockedPost(page);
        List<BlockDto.BlockPostResponse> responsePost = blockedPost.stream()
                .map(post -> BlockDto.BlockPostResponse.builder()
                        .post(post)
                        .build())
                .toList();

        return ResponseEntity.ok().body(responsePost);
    }

    @Admin
    @GetMapping("/comment/block/{page}")
    public ResponseEntity<List<BlockDto.BlockCommentResponse>> getBlockedCommentList(@PathVariable Integer page){
        List<Comment> blockedComment = commentService.findBlockedComment(page);
        List<BlockDto.BlockCommentResponse> responseComment = blockedComment.stream()
                .map(comment -> BlockDto.BlockCommentResponse.builder()
                        .comment(comment)
                        .build())
                .toList();

        return ResponseEntity.ok().body(responseComment);
    }

    @Admin
    @GetMapping("/reply/block/{page}")
    public ResponseEntity<List<BlockDto.BlockReplyResponse>> getBlockedReplyList(@PathVariable Integer page){
        List<Reply> blockedReply = replyService.findBlockedReply(page);
        List<BlockDto.BlockReplyResponse> responseReply = blockedReply.stream()
                .map(reply -> BlockDto.BlockReplyResponse.builder()
                        .reply(reply)
                        .build())
                .toList();

        return ResponseEntity.ok().body(responseReply);
    }

    @Admin
    @GetMapping("/post/block/read/{blockPostId}")
    public ResponseEntity<?> getBlockPostDetail(@PathVariable Long blockPostId){
        Post post = postService.findSinglePost(blockPostId);

        ArrayList<CommentDto.ReadAdminCommentResponse> responseCommentList = new ArrayList<>();
        PostDto.ReadAdminPostResponse responsePost = getReadAdminPostResponse(post, responseCommentList);

        return ResponseEntity.ok().body(responsePost);
    }

    @Admin
    @GetMapping("/reply/block/read/{blockReplyId}")
    public ResponseEntity<?> getBlockReplyDetail(@PathVariable Long blockReplyId){
        Reply replyByReplyId = replyService.findReplyByReplyId(blockReplyId);
        Post post = postService.findSinglePost(replyByReplyId.getCommentId().getPostId().getPostId());

        ArrayList<CommentDto.ReadAdminCommentResponse> responseCommentList = new ArrayList<>();
        PostDto.ReadAdminPostResponse responsePost = getReadAdminPostResponse(post, responseCommentList);

        return ResponseEntity.ok().body(responsePost);
    }

    private PostDto.ReadAdminPostResponse getReadAdminPostResponse(Post post, ArrayList<CommentDto.ReadAdminCommentResponse> responseCommentList) {
        List<Comment> commentList = commentService.findBlockedCommentDetail(post);
        for (Comment comment : commentList) {
            List<Reply> replyList = replyService.findBlockedReplyDetail(comment);
            List<ReplyDto.ReadAdminReplyResponse> replyResponses = replyList.stream()
                    .map(reply -> ReplyDto.ReadAdminReplyResponse.builder()
                            .reply(reply)
                            .build())
                    .toList();

            CommentDto.ReadAdminCommentResponse commentResponse =
                    CommentDto.ReadAdminCommentResponse.builder()
                    .comment(comment)
                    .replyList(replyResponses)
                    .build();

            responseCommentList.add(commentResponse);
        }

        return PostDto.ReadAdminPostResponse.builder()
                .post(post)
                .commentList(responseCommentList)
                .build();
    }

    @Admin
    @PostMapping("/post/unBlock/{unBlockPostId}")
    public ResponseEntity<?> unBlockPost(@PathVariable Long unBlockPostId){
        Post post = postService.findFetchMember(unBlockPostId);
        unLockedMember(post.getMemberId().getEmail());
        postService.setUnLocked(post);

        return ResponseEntity.ok().build();
    }

    @Admin
    @PostMapping("/comment/unBlock/{unBlockCommentId}")
    public ResponseEntity<?> unBlockComment(@PathVariable Long unBlockCommentId){
        Comment comment = commentService.findFetchMember(unBlockCommentId);
        unLockedMember(comment.getMemberId().getEmail());
        commentService.setUnLocked(comment);

        return ResponseEntity.ok().build();
    }

    @Admin
    @PostMapping("/reply/unBlock/{unBlockReplyId}")
    public ResponseEntity<?> unBlockReply(@PathVariable Long unBlockReplyId){
        Reply reply = replyService.findFetchMember(unBlockReplyId);
        unLockedMember(reply.getMemberId().getEmail());
        replyService.setUnLocked(reply);

        return ResponseEntity.ok().build();
    }

    private void unLockedMember(String userEmail) {
        Member member = memberService.findByEmail(userEmail);
        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        lockMember.ifPresent(lockMemberService::deleteLockMember);
        memberService.subtractReportCount(member);
    }
}
