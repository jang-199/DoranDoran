package com.dorandoran.doranserver.domain.comment.controller;

import com.dorandoran.doranserver.global.util.CommentResponseUtils;
import com.dorandoran.doranserver.global.util.annotation.Trace;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.service.CommentLikeService;
import com.dorandoran.doranserver.domain.comment.service.CommentService;
import com.dorandoran.doranserver.domain.comment.service.ReplyService;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.member.service.LockMemberService;
import com.dorandoran.doranserver.domain.member.service.MemberBlockListService;
import com.dorandoran.doranserver.domain.member.service.MemberService;
import com.dorandoran.doranserver.domain.notification.service.FirebaseService;
import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.post.service.AnonymityMemberService;
import com.dorandoran.doranserver.domain.post.service.PopularPostService;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.global.util.BlockMemberFilter;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Timed
@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final ReplyService replyService;
    private final PopularPostService popularPostService;
    private final AnonymityMemberService anonymityMemberService;
    private final LockMemberService lockMemberService;
    private final MemberBlockListService memberBlockListService;
    private final BlockMemberFilter blockMemberFilter;
    private final FirebaseService firebaseService;
    private final CommentResponseUtils commentResponseUtils;
    @Value("${doran.ip.address}")
    String ipAddress;

    @Trace
    @GetMapping("/comment")
    public ResponseEntity<?> inquiryComment(@RequestParam("postId") Long postId,
                                            @RequestParam("commentId") Long commentId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Post post = postService.findSinglePost(postId);
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);

        List<Comment> comments = commentService.findNextComments(postId, commentId);

        HashMap<Comment, Long> commentLikeCntHashMap = commentLikeService.findCommentLikeCnt(comments);
        HashMap<Comment, Boolean> commentLikeResultHashMap = commentLikeService.findCommentLikeResult(userEmail, comments);
        List<CommentDto.ReadCommentResponse> commentDetailDtoList = commentResponseUtils.makeCommentAndReplyList(userEmail, post, anonymityMemberList, comments, memberBlockListByBlockingMember, commentLikeResultHashMap, commentLikeCntHashMap);
        return ResponseEntity.ok().body(commentDetailDtoList);
    }


    @Trace
    @PostMapping("/comment")
    ResponseEntity<?> saveComment(@RequestBody CommentDto.CreateComment createCommentDto,
                              @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("정지된 회원은 댓글을 작성할 수 없습니다.");
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }

        Post post = postService.findSinglePost(createCommentDto.getPostId());
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(post);
        Long nextIndex = anonymityMembers.size() + 1L;

        Comment comment = new Comment().toEntity(createCommentDto, post, member);
        commentService.saveComment(comment);

        if (!post.getMemberId().equals(member) && post.getMemberId().checkNotification()){
            firebaseService.notifyComment(post.getMemberId(), comment);
        }

        Post singlePost = postService.findSinglePost(createCommentDto.getPostId());
        List<Comment> commentByPost = commentService.findCommentByPost(singlePost);
        if (commentByPost.size() >= 10 && popularPostService.findPopularPostByPost(singlePost).isEmpty()) {
            PopularPost build = PopularPost.builder().postId(singlePost).build();
            popularPostService.savePopularPost(build);
        }

        AnonymityMember anonymityMember = new AnonymityMember().toEntity(userEmail, post, nextIndex);
        anonymityMemberService.checkAndSave(createCommentDto.getAnonymity(), anonymityMembers, userEmail, anonymityMember);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Trace
    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestBody CommentDto.DeleteComment commentDeleteDto,
                                           @AuthenticationPrincipal UserDetails userDetails){
        Comment comment = commentService.findCommentByCommentId(commentDeleteDto.getCommentId());
        if (comment.getMemberId().getEmail().equals(userDetails.getUsername())) {
            commentService.setCheckDelete(comment);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("댓글 작성자가 아닙니다.");
        }
    }

    @Trace
    @PostMapping("/comment/like")
    ResponseEntity<?> commentLike(@RequestBody CommentDto.LikeComment commentLikeDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = commentService.findCommentByCommentId(commentLikeDto.getCommentId());
        Member member = memberService.findByEmail(userDetails.getUsername());
        Optional<CommentLike> commentLike = commentLikeService.findCommentLikeOne(userDetails.getUsername(), comment);

        if (comment.getMemberId().equals(member)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자신의 댓글에 추천은 불가능합니다.");
        }

        commentLikeService.checkCommentLike(commentLikeDto, userDetails, comment, member, commentLike);

        if (commentLike.isEmpty() && comment.getMemberId().checkNotification()) {
            firebaseService.notifyCommentLike(comment.getMemberId(), comment);
        }

        return ResponseEntity.noContent().build();
    }

    @Trace
    @GetMapping("/reply")
    public ResponseEntity<?> inquiryReply(@RequestParam("postId") Long postId,
                                          @RequestParam("commentId") Long commentId,
                                          @RequestParam("replyId") Long replyId,
                                          @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        Post post = postService.findSinglePost(postId);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);
        Member member = memberService.findByEmail(userEmail);
        List<Member> memberBlockListByBlockingMember = memberBlockListService.findMemberBlockListByBlockingMember(member);
        List<Reply> replies = replyService.findNextReplies(commentId, replyId);
        List<Reply> replyList = blockMemberFilter.replyFilter(replies, memberBlockListByBlockingMember);

        List<ReplyDto.ReadReplyResponse> replyDetailDtoList = commentResponseUtils.makeReplyList(userEmail, post, anonymityMemberList, replyList);

        return ResponseEntity.ok().body(replyDetailDtoList);
    }

    @Trace
    @Transactional
    @PostMapping("/reply")
    public ResponseEntity<?> saveReply(@RequestBody ReplyDto.CreateReply replyDto,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        Comment comment = commentService.findCommentByCommentId(replyDto.getCommentId());
        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return new ResponseEntity<>("정지된 회원은 댓글을 작성할 수 없습니다.", HttpStatus.BAD_REQUEST);
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }

        comment.setCountReply(comment.getCountReply()+1);
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(comment.getPostId());
        Long nextIndex = anonymityMembers.size() + 1L;

        Reply buildReply = new Reply().toEntity(replyDto, comment, member);

        replyService.saveReply(buildReply);

        List<Member> replyMemberList = replyService.findReplyMemberByComment(comment);
        replyMemberList.add(comment.getMemberId());
        List<Member> fcmMemberList = checkMyComment(replyMemberList, member);
        if (!fcmMemberList.isEmpty()) {
            firebaseService.notifyReply(fcmMemberList, buildReply);
        }

        AnonymityMember anonymityMember = new AnonymityMember().toEntity(userEmail, comment.getPostId(), nextIndex);
        anonymityMemberService.checkAndSave(replyDto.getAnonymity(), anonymityMembers, userEmail, anonymityMember);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Trace
    @DeleteMapping("/reply")
    public ResponseEntity<?> replyDelete(@RequestBody ReplyDto.DeleteReply replyDeleteDto,
                                         @AuthenticationPrincipal UserDetails userDetails){
        Reply reply = replyService.findReplyByReplyId(replyDeleteDto.getReplyId()).orElseThrow(() -> new NoSuchElementException("에러 발생"));
        if (reply.getMemberId().getEmail().equals(userDetails.getUsername())){
            replyService.setCheckDelete(reply);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("대댓글 작성자가 아닙니다.");
        }
    }

    private static List<Member> checkMyComment(List<Member> memberList, Member writeMember) {
        return memberList.stream()
                .distinct()
                .filter((member) ->
                        !member.equals(writeMember)
                        && member.checkNotification())
                .collect(Collectors.toList());
    }
}