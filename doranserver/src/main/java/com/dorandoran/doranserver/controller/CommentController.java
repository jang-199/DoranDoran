package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.dto.postDetail.CommentDetailDto;
import com.dorandoran.doranserver.dto.postDetail.ReplyDetailDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.exception.CannotFindReplyException;
import com.dorandoran.doranserver.service.*;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private final CommonService commonService;

    @GetMapping("/comment")
    public ResponseEntity<?> inquiryComment(@RequestParam("postId") Long postId,
                                            @RequestParam("commentId") Long commentId,
                                            @RequestParam("userEmail") String userEmail) {
        Post post = postService.findSinglePost(postId);
        log.info("글쓴이 email : {}", post.getMemberId().getEmail());
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);
        List<Comment> comments = commentService.findNextComments(postId, commentId);
        List<CommentDetailDto> commentDetailDtoList = makeCommentAndReplyList(userEmail, post, anonymityMemberList, comments);
        return ResponseEntity.ok().body(commentDetailDtoList);
    }



    @PostMapping("/comment")
    ResponseEntity<?> comment(@RequestBody CommentDto commentDto) {
        Member member = memberService.findByEmail(commentDto.getEmail());

        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return new ResponseEntity<>("정지된 회원은 댓글을 작성할 수 없습니다.", HttpStatus.BAD_REQUEST);
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }
        Post post = postService.findSinglePost(commentDto.getPostId());
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(post);
        Long nextIndex = anonymityMembers.size() + 1L;

        log.info("사용자 {}의 댓글 작성", commentDto.getEmail());
        Comment comment = Comment.builder()
                .comment(commentDto.getComment())
                .commentTime(LocalDateTime.now())
                .postId(post)
                .memberId(member)
                .anonymity(commentDto.getAnonymity())
                .checkDelete(Boolean.FALSE)
                .secretMode(commentDto.getSecretMode())
                .isLocked(Boolean.FALSE)
                .build();
        commentService.saveComment(comment);

        //인기 있는 글 생성
        Post singlePost = postService.findSinglePost(commentDto.getPostId());
        List<Comment> commentByPost = commentService.findCommentByPost(singlePost);
        if (commentByPost.size() >= 10 && popularPostService.findPopularPostByPost(singlePost).size() == 0) {
            PopularPost build = PopularPost.builder().postId(singlePost).build();
            popularPostService.savePopularPost(build);
        }


        if (commentDto.getAnonymity().equals(Boolean.TRUE)) {
            if (anonymityMembers.contains(commentDto.getEmail())) {
                log.info("이미 익명 테이블에 저장된 사용자입니다.");
            } else {
                AnonymityMember anonymityMember = AnonymityMember.builder()
                        .userEmail(member.getEmail())
                        .postId(post)
                        .anonymityIndex(nextIndex)
                        .build();
                anonymityMemberService.save(anonymityMember);
                log.info("익명 테이블에 저장");
            }
        }
            return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/comment-delete")
    @Transactional
    public ResponseEntity<?> deleteComment(@RequestBody CommentDeleteDto commentDeleteDto){
        Optional<Comment> comment = commentService.findCommentByCommentId(commentDeleteDto.getCommentId());
        if (comment.get().getMemberId().getEmail().equals(commentDeleteDto.getUserEmail())) {
            //댓글 checkDelete 삭제로 표시
            comment.get().setCheckDelete(Boolean.TRUE);
            log.info("댓글 숨김 처리");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.info("댓글을 작성한 사용자가 아닙니다.");
            return new ResponseEntity<>("댓글 작성자가 아닙니다.",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comment-like")
    ResponseEntity<?> commentLike(@RequestBody CommentLikeDto commentLikeDto) {
        Optional<Comment> comment = commentService.findCommentByCommentId(commentLikeDto.getCommentId());
        Member member = memberService.findByEmail(commentLikeDto.getUserEmail());

        List<CommentLike> commentLikeList = commentLikeService.findByCommentId(comment.get());
        for (CommentLike commentLike : commentLikeList) {
            if (commentLike.getMemberId().getEmail().equals(commentLikeDto.getUserEmail())) {
                commentLikeService.deleteCommentLike(commentLike);
                log.info("{} 글의 {} 댓글 공감 취소", commentLikeDto.getPostId(), commentLike.getCommentId().getCommentId());
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        CommentLike commentLikeBuild = CommentLike.builder()
                .commentId(comment.get())
                .memberId(member)
                .build();
        commentLikeService.saveCommentLike(commentLikeBuild);
        log.info("{} 글의 {} 댓글 공감", commentLikeDto.getPostId(), comment.get().getCommentId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/reply")
    public ResponseEntity<?> inquiryReply(@RequestParam("postId") Long postId,
                                          @RequestParam("commentId") Long commentId,
                                          @RequestParam("replyId") Long replyId,
                                          @RequestParam("userEmail") String userEmail){
        Post post = postService.findSinglePost(postId);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post);
        List<Reply> replies = replyService.findNextReplies(commentId, replyId);

        List<ReplyDetailDto> replyDetailDtoList = makeReplyList(userEmail, post, anonymityMemberList, replies);

        return ResponseEntity.ok().body(replyDetailDtoList);
    }

    @Transactional
    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody ReplyDto replyDto) {
        Optional<Comment> comment = commentService.findCommentByCommentId(replyDto.getCommentId());
        Member member = memberService.findByEmail(replyDto.getUserEmail());

        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return new ResponseEntity<>("정지된 회원은 댓글을 작성할 수 없습니다.", HttpStatus.BAD_REQUEST);
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }
        if (comment.isPresent()) {
            comment.get().setCountReply(comment.get().getCountReply()+1);
            List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(comment.get().getPostId());
            Long nextIndex = anonymityMembers.size() + 1L;

            Reply buildReply = Reply.builder()
                    .reply(replyDto.getReply())
                    .ReplyTime(LocalDateTime.now())
                    .anonymity(replyDto.getAnonymity())
                    .commentId(comment.get())
                    .memberId(member)
                    .checkDelete(Boolean.FALSE)
                    .secretMode(replyDto.getSecretMode())
                    .isLocked(Boolean.FALSE)
                    .build();

            replyService.saveReply(buildReply);

            if (replyDto.getAnonymity().equals(Boolean.TRUE)) {
                if (anonymityMembers.contains(replyDto.getUserEmail())) {
                    log.info("이미 익명 테이블에 저장된 사용자입니다.");
                } else {
                    AnonymityMember anonymityMember = AnonymityMember.builder()
                            .userEmail(member.getEmail())
                            .postId(comment.get().getPostId())
                            .anonymityIndex(nextIndex)
                            .build();
                    anonymityMemberService.save(anonymityMember);
                    log.info("익명 테이블에 저장");
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.info("해당 상위 댓글이 없거나 존재하지 않는 멤버입니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reply-delete")
    @Transactional
    public ResponseEntity<?> replyDelete(@RequestBody ReplyDeleteDto replyDeleteDto){
        Reply reply = replyService.findReplyByReplyId(replyDeleteDto.getReplyId()).orElseThrow(() -> new CannotFindReplyException("에러 발생"));
        if (reply.getMemberId().getEmail().equals(replyDeleteDto.getUserEmail())){
            //대댓글 checkDelete 삭제로 표시
            reply.setCheckDelete(Boolean.TRUE);
            log.info("대댓글 숨김 처리");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.info("대댓글을 작성한 사용자가 아닙니다.");
            return new ResponseEntity<>("대댓글 작성자가 아닙니다.",HttpStatus.BAD_REQUEST);
        }
    }

    private List<CommentDetailDto> makeCommentAndReplyList(String userEmail, Post post, List<String> anonymityMemberList, List<Comment> comments) {
        List<CommentDetailDto> commentDetailDtoList = new ArrayList<>();
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                //대댓글 10개 저장 로직
                List<Reply> replies = replyService.findFirstRepliesFetchMember(comment);
                List<ReplyDetailDto> replyDetailDtoList = makeReplyList(userEmail, post, anonymityMemberList, replies);
                Collections.reverse(replyDetailDtoList);

                //댓글 10개 저장 로직
                makeCommentList(userEmail, post, anonymityMemberList, commentDetailDtoList, comment, replyDetailDtoList);
                Collections.reverse(commentDetailDtoList);
            }
        }

        return commentDetailDtoList;
    }

    private void makeCommentList(String userEmail, Post post, List<String> anonymityMemberList, List<CommentDetailDto> commentDetailDtoList, Comment comment, List<ReplyDetailDto> replyDetailDtoList) {
        Integer commentLikeCnt = commentLikeService.findCommentLikeCnt(comment);
        Boolean commentLikeResult = commentLikeService.findCommentLikeResult(userEmail, comment);
        Boolean isCommentWrittenByMember = Boolean.FALSE;
        if (commonService.compareEmails(comment.getMemberId().getEmail(), userEmail)) {
            isCommentWrittenByMember = Boolean.TRUE;
        }

        CommentDetailDto commentDetailDto = CommentDetailDto.builder()
                .comment(comment)
                .content(comment.getComment())
                .commentLikeResult(commentLikeResult)
                .commentLikeCnt(commentLikeCnt)
                .isWrittenByMember(isCommentWrittenByMember)
                .replies(replyDetailDtoList)
                .build();
        commentService.checkSecretComment(commentDetailDto, post, comment, userEmail);
        commentService.checkCommentAnonymityMember(anonymityMemberList, comment, commentDetailDto);
        commentDetailDtoList.add(commentDetailDto);
    }

    private List<ReplyDetailDto> makeReplyList(String userEmail, Post post, List<String> anonymityMemberList, List<Reply> replies) {
        List<ReplyDetailDto> replyDetailDtoList = new ArrayList<>();
        log.info("대댓글 로직 실행");
        for (Reply reply : replies) {
            Boolean isReplyWrittenByUser = Boolean.FALSE;
            if (commonService.compareEmails(reply.getMemberId().getEmail(), userEmail)) {
                isReplyWrittenByUser = Boolean.TRUE;
            }
            ReplyDetailDto replyDetailDto = ReplyDetailDto.builder()
                    .reply(reply)
                    .content(reply.getReply())
                    .isWrittenByMember(isReplyWrittenByUser)
                    .build();
            replyService.checkSecretReply(replyDetailDto, post, reply, userEmail);
            replyService.checkReplyAnonymityMember(anonymityMemberList, reply, replyDetailDto);
            replyDetailDtoList.add(replyDetailDto);
        }
        return replyDetailDtoList;
    }

}