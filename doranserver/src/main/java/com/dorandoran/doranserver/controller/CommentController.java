package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.exception.CannotFindReplyException;
import com.dorandoran.doranserver.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Tag(name = "댓글 관련 API", description = "CommentController")
@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final MemberServiceImpl memberService;
    private final PostServiceImpl postService;
    private final CommentServiceImpl commentService;
    private final CommentLikeServiceImpl commentLikeService;
    private final ReplyServiceImpl replyService;
    private final PopularPostServiceImpl popularPostService;
    private final AnonymityMemberService anonymityMemberService;

    @Tag(name = "댓글 관련 API")
    @Operation(summary = "댓글 생성", description = "댓글을 생성, " +
                                                   "요청한 글에 달린 댓글이 10개 이상 시 인기 있는 글을 추가," +
                                                   "익명으로 작성 시 익명 테이블에 추가하는 API입니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "댓글 생성 성공")})
    @PostMapping("/comment")
    ResponseEntity<?> comment(@Parameter(description = "댓글 저장 시 필요한 데이터") @RequestBody CommentDto commentDto) {
        Optional<Member> member = memberService.findByEmail(commentDto.getEmail());
        Optional<Post> post = postService.findSinglePost(commentDto.getPostId());
        List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(post.get());
        Long nextIndex = anonymityMembers.size() + 1L;

        log.info("사용자 {}의 댓글 작성", commentDto.getEmail());
        Comment comment = Comment.builder()
                .comment(commentDto.getComment())
                .commentTime(LocalDateTime.now())
                .postId(post.get())
                .memberId(member.get())
                .anonymity(commentDto.getAnonymity())
                .checkDelete(Boolean.FALSE)
                .build();
        commentService.saveComment(comment);

        //인기 있는 글 생성
        Optional<Post> singlePost = postService.findSinglePost(commentDto.getPostId());
        if (singlePost.isPresent()) {
            List<Comment> commentByPost = commentService.findCommentByPost(singlePost.get());
            if (commentByPost.size() >= 10) {
                PopularPost build = PopularPost.builder().postId(singlePost.get()).build();
                popularPostService.savePopularPost(build);
            }
        }

        if (commentDto.getAnonymity().equals(Boolean.TRUE)) {
            if (anonymityMembers.contains(commentDto.getEmail())) {
                log.info("이미 익명 테이블에 저장된 사용자입니다.");
            } else {
                AnonymityMember anonymityMember = AnonymityMember.builder()
                        .userEmail(member.get().getEmail())
                        .postId(post.get())
                        .anonymityIndex(nextIndex)
                        .build();
                anonymityMemberService.save(anonymityMember);
                log.info("익명 테이블에 저장");
            }
        }
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "댓글 관련 API")
    @Operation(summary = "댓글 삭제", description = "댓글 삭제하는 API로 실제로 삭제하는 것이 아닌 숨김 처리를 통해 사용자가 보지 못하게 한다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
                   @ApiResponse(responseCode = "400", description = "댓글 작성 사용자와 삭제 요청한 사용자가 다를 시 실패")})
    @PostMapping("/comment-delete")
    @Transactional
    public ResponseEntity<?> deleteComment(@Parameter(description = "댓글 삭제 시 필요한 데이터") @RequestBody CommentDeleteDto commentDeleteDto){
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

    @Tag(name = "댓글 관련 API")
    @Operation(summary = "댓글 공감", description = "댓글 공감하는 API로 기존의 댓글 공감 했을 시 취소합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "댓글 공감이나 공감 취소 성공")})
    @PostMapping("/comment-like")
    ResponseEntity<?> commentLike(@Parameter(description = "댓글 공감 시 필요한 데이터")@RequestBody CommentLikeDto commentLikeDto) {
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

    @Tag(name = "댓글 관련 API")
    @Operation(summary = "대댓글 작성", description = "대댓글 작성, 익명 선택 시 익명 테이블에 저장하는 API입니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
                   @ApiResponse(responseCode = "400", description = "댓글 작성 실패")})
    @PostMapping("/reply")
    public ResponseEntity<?> reply(@Parameter(description = "대댓글 작성 시 필요한 데이터") @RequestBody ReplyDto replyDto) {
        Optional<Comment> comment = commentService.findCommentByCommentId(replyDto.getCommentId());
        Optional<Member> member = memberService.findByEmail(replyDto.getUserEmail());

        if (comment.isPresent() && member.isPresent()) {
            List<String> anonymityMembers = anonymityMemberService.findAllUserEmail(comment.get().getPostId());
            Long nextIndex = anonymityMembers.size() + 1L;

            Reply buildReply = Reply.builder()
                    .reply(replyDto.getReply())
                    .ReplyTime(LocalDateTime.now())
                    .anonymity(replyDto.getAnonymity())
                    .commentId(comment.get())
                    .memberId(member.get())
                    .checkDelete(Boolean.FALSE)
                    .build();

            replyService.saveReply(buildReply);

            if (replyDto.getAnonymity().equals(Boolean.TRUE)) {
                if (anonymityMembers.contains(replyDto.getUserEmail())) {
                    log.info("이미 익명 테이블에 저장된 사용자입니다.");
                } else {
                    AnonymityMember anonymityMember = AnonymityMember.builder()
                            .userEmail(member.get().getEmail())
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

    @Tag(name = "댓글 관련 API")
    @Operation(summary = "대댓글 삭제", description = "대댓글을 삭제하는 API로 실제로 삭제하는 것이 아닌 숨김 처리를 통해 사용자가 보지 못하게 한다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "대댓글 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "대댓글 작성 사용자와 삭제 요청한 사용자가 다를 시 실패")})
    @PostMapping("/reply-delete")
    @Transactional
    public ResponseEntity<?> replyDelete(@Parameter(description = "대댓글 삭제 시 필요한 데이터")@RequestBody ReplyDeleteDto replyDeleteDto){
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