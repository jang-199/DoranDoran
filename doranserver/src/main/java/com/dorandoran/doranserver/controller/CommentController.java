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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
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

    @GetMapping("/comment")
    public ResponseEntity<?> inquiryComment(@RequestParam("postId") Long postId,
                                            @RequestParam("commentId") Long commentId,
                                            @RequestParam("userEmail") String userEmail) {
        Optional<Post> post = postService.findSinglePost(postId);
        log.info("글쓴이 email : {}", post.get().getMemberId().getEmail());
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post.get());
        List<Comment> comments = commentService.findNextComments(postId, commentId);
        List<CommentDetailDto> commentDetailDtoList = new ArrayList<>();
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                Integer commentLikeCnt = commentLikeService.findCommentLikeCnt(comment);
                Boolean commentLikeResult = commentLikeService.findCommentLikeResult(userEmail, comment);

                //대댓글 10개 저장 로직
                List<Reply> replies = replyService.findFirstReplies(comment);
                List<ReplyDetailDto> replyDetailDtoList = new ArrayList<>();
                for (Reply reply : replies) {
                    ReplyDetailDto replyDetailDto = null;

                    //대댓글 자신이 썼는 지 확인
                    Boolean isReplyWrittenByMember = Boolean.FALSE;
                    if (reply.getMemberId().getEmail().equals(userEmail))
                        isReplyWrittenByMember = Boolean.TRUE;

                    //비밀 대댓글에 따른 저장 로직
                    if (reply.getSecretMode() == Boolean.TRUE) {
                        log.info("{}는 비밀 댓글로직 실행", reply.getReplyId());
                        if (userEmail.equals(post.get().getMemberId().getEmail())) {
                            //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                            replyDetailDto = new ReplyDetailDto(reply, reply.getReply(), isReplyWrittenByMember);
                            log.info("글쓴이입니다.");
                        } else {
                            //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                            replyDetailDto =
                                    (reply.getMemberId().getEmail().equals(userEmail))
                                            ? new ReplyDetailDto(reply, reply.getReply(), isReplyWrittenByMember)
                                            : new ReplyDetailDto(reply, "비밀 댓글입니다.", isReplyWrittenByMember);
                            log.info("글쓴이가 아닙니다.");
                        }
                    }else {
                        log.info("{}는 비밀 댓글로직 실행 안함",reply.getReplyId());
                        replyDetailDto = new ReplyDetailDto(reply, reply.getReply(), isReplyWrittenByMember);
                    }
                    if (anonymityMemberList.contains(reply.getMemberId().getEmail())) {
                        int replyAnonymityIndex = anonymityMemberList.indexOf(reply.getMemberId().getEmail()) + 1;
                        log.info("{}의 index값은 {}이다", reply.getMemberId().getEmail(), replyAnonymityIndex);
                        replyDetailDto.setReplyAnonymityNickname("익명" + replyAnonymityIndex);
                    }
                    replyDetailDtoList.add(replyDetailDto);
                }
                Collections.reverse(replyDetailDtoList);

                //내가 쓴 댓글인지 확인
                Boolean isCommentWrittenByMember = Boolean.FALSE;
                if (comment.getMemberId().getEmail().equals(userEmail))
                    isCommentWrittenByMember = Boolean.TRUE;

                //비밀 댓글에 따른 저장 로직
                CommentDetailDto commentDetailDto = null;
                if (comment.getSecretMode() == Boolean.TRUE) {
                    if (userEmail.equals(post.get().getMemberId().getEmail())) {
                        //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                        commentDetailDto = new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, isCommentWrittenByMember, replyDetailDtoList);
                    } else {
                        //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                        commentDetailDto =
                                        (comment.getMemberId().getEmail().equals(userEmail))
                                        ? new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, isCommentWrittenByMember, replyDetailDtoList)
                                        : new CommentDetailDto(comment, "비밀 댓글입니다.", commentLikeCnt, commentLikeResult, isCommentWrittenByMember, replyDetailDtoList);
                    }
                }else {
                    commentDetailDto = new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, isCommentWrittenByMember, replyDetailDtoList);
                }

                if (anonymityMemberList.contains(comment.getMemberId().getEmail())) {
                    int commentAnonymityIndex = anonymityMemberList.indexOf(comment.getMemberId().getEmail()) + 1;
                    log.info("{}의 index값은 {}이다", comment.getMemberId().getEmail(), commentAnonymityIndex);
                    commentDetailDto.setCommentAnonymityNickname("익명" + commentAnonymityIndex);
                }
                commentDetailDtoList.add(commentDetailDto);
            }
        }
        Collections.reverse(commentDetailDtoList);
        return ResponseEntity.ok().body(commentDetailDtoList);
    }

    @PostMapping("/comment")
    ResponseEntity<?> comment(@RequestBody CommentDto commentDto) {
        Member member = memberService.findByEmail(commentDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        Optional<LockMember> lockMember = lockMemberService.findLockMember(member);
        if (lockMember.isPresent()){
            if (lockMemberService.checkCurrentLocked(lockMember.get())){
                return new ResponseEntity<>("정지된 회원은 댓글을 작성할 수 없습니다.", HttpStatus.BAD_REQUEST);
            }else {
                lockMemberService.deleteLockMember(lockMember.get());
            }
        }

        Optional<Post> post = postService.findSinglePost(commentDto.getPostId());
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(post.get());
        Long nextIndex = anonymityMembers.size() + 1L;

        log.info("사용자 {}의 댓글 작성", commentDto.getEmail());
        Comment comment = Comment.builder()
                .comment(commentDto.getComment())
                .commentTime(LocalDateTime.now())
                .postId(post.get())
                .memberId(member)
                .anonymity(commentDto.getAnonymity())
                .checkDelete(Boolean.FALSE)
                .secretMode(commentDto.getSecretMode())
                .isLocked(Boolean.FALSE)
                .build();
        commentService.saveComment(comment);

        //인기 있는 글 생성
        Optional<Post> singlePost = postService.findSinglePost(commentDto.getPostId());
        if (singlePost.isPresent()) {
            List<Comment> commentByPost = commentService.findCommentByPost(singlePost.get());
            if (commentByPost.size() >= 10 && popularPostService.findPopularPostByPost(singlePost.get()).size() == 0) {
                PopularPost build = PopularPost.builder().postId(singlePost.get()).build();
                popularPostService.savePopularPost(build);
            }
        }

        if (commentDto.getAnonymity().equals(Boolean.TRUE)) {
            if (anonymityMembers.contains(commentDto.getEmail())) {
                log.info("이미 익명 테이블에 저장된 사용자입니다.");
            } else {
                AnonymityMember anonymityMember = AnonymityMember.builder()
                        .userEmail(member.getEmail())
                        .postId(post.get())
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
        Optional<Member> member = memberService.findByEmail(commentLikeDto.getUserEmail());

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
                .memberId(member.get())
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
        Optional<Post> post = postService.findSinglePost(postId);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post.get());
        List<Reply> replies = replyService.findNextReplies(commentId, replyId);

        List<ReplyDetailDto> replyDtoList = new ArrayList<>();
        for (Reply reply : replies) {
            ReplyDetailDto replyDetailDto = null;

            //대댓글 자신이 썼는 지 확인
            Boolean isReplyWrittenByMember = Boolean.FALSE;
            if (reply.getMemberId().getEmail().equals(userEmail))
                isReplyWrittenByMember = Boolean.TRUE;

            //비밀 대댓글에 따른 저장 로직
            if (reply.getSecretMode() == Boolean.TRUE) {
                if (userEmail.equals(post.get().getMemberId().getEmail())) {
                    //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                    replyDetailDto = new ReplyDetailDto(reply, reply.getReply() ,isReplyWrittenByMember);
                } else {
                    //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                    replyDetailDto =
                            (reply.getMemberId().getEmail().equals(userEmail))
                                    ? new ReplyDetailDto(reply, reply.getReply(), isReplyWrittenByMember)
                                    : new ReplyDetailDto(reply, "비밀 댓글입니다.", isReplyWrittenByMember);
                }
            }else {
                replyDetailDto = new ReplyDetailDto(reply, reply.getReply(), isReplyWrittenByMember);
            }

            if (anonymityMemberList.contains(reply.getMemberId().getEmail())) {
                int replyAnonymityIndex = anonymityMemberList.indexOf(reply.getMemberId().getEmail()) + 1;
                log.info("{}의 index값은 {}이다", reply.getMemberId().getEmail(), replyAnonymityIndex);
                replyDetailDto.setReplyAnonymityNickname("익명" + replyAnonymityIndex);
            }
            replyDtoList.add(replyDetailDto);
        }
        Collections.reverse(replyDtoList);

        return ResponseEntity.ok().body(replyDtoList);
    }

    @Transactional
    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody ReplyDto replyDto) {
        Optional<Comment> comment = commentService.findCommentByCommentId(replyDto.getCommentId());
        Member member = memberService.findByEmail(replyDto.getUserEmail()).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

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


}