package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.*;
import com.dorandoran.doranserver.dto.postDetail.CommentDetailDto;
import com.dorandoran.doranserver.dto.postDetail.PostDetailDto;
import com.dorandoran.doranserver.dto.postDetail.ReplyDetailDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.exception.CannotFindReplyException;
import com.dorandoran.doranserver.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Operation(summary = "댓글 최대 10개 조회", description = "댓글 pk값을 통해서 현재 조회되지 않은 댓글을 최대 10개를 반환하는 API입니다." +
            "\n\n Ex.현재 조회된 댓글의 최소 Pk값이 25라면 그 전의 PK값인 20,22,23의 댓글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "최대 10개의 댓글을 반환합니다.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = CommentDetailDto.class)))
    @GetMapping("/comment")
    public ResponseEntity<?> inquiryComment(@Parameter(description = "글 pk값", required = true) @RequestParam("postId") Long postId,
                                            @Parameter(description = "현재 조회된 최소 댓글 pk값", required = true) @RequestParam("commentId") Long commentId,
                                            @Parameter(description = "사용자 email", required = true) @RequestParam("userEmail") String userEmail) {
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
                    //비밀 대댓글에 따른 저장 로직
                    if (reply.getSecretMode() == Boolean.TRUE) {
                        log.info("{}는 비밀 댓글로직 실행", reply.getReplyId());
                        if (userEmail.equals(post.get().getMemberId().getEmail())) {
                            //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                            replyDetailDto = new ReplyDetailDto(reply, reply.getReply());
                            log.info("글쓴이입니다.");
                        } else {
                            //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                            replyDetailDto =
                                    (reply.getMemberId().getEmail().equals(userEmail))
                                            ? new ReplyDetailDto(reply, reply.getReply())
                                            : new ReplyDetailDto(reply, "비밀 댓글입니다.");
                            log.info("글쓴이가 아닙니다.");
                        }
                    }else {
                        log.info("{}는 비밀 댓글로직 실행 안함",reply.getReplyId());
                        replyDetailDto = new ReplyDetailDto(reply, reply.getReply());
                    }
                    if (anonymityMemberList.contains(reply.getMemberId().getEmail())) {
                        int replyAnonymityIndex = anonymityMemberList.indexOf(reply.getMemberId().getEmail()) + 1;
                        log.info("{}의 index값은 {}이다", reply.getMemberId().getEmail(), replyAnonymityIndex);
                        replyDetailDto.setReplyAnonymityNickname("익명" + replyAnonymityIndex);
                    }
                    replyDetailDtoList.add(replyDetailDto);
                }
                Collections.reverse(replyDetailDtoList);

                //비밀 댓글에 따른 저장 로직
                CommentDetailDto commentDetailDto = null;
                if (comment.getSecretMode() == Boolean.TRUE) {
                    if (userEmail.equals(post.get().getMemberId().getEmail())) {
                        //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                        commentDetailDto = new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, replyDetailDtoList);
                    } else {
                        //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                        commentDetailDto =
                                (comment.getMemberId().getEmail() == userEmail)
                                        ? new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, replyDetailDtoList)
                                        : new CommentDetailDto(comment, "비밀 댓글입니다.", commentLikeCnt, commentLikeResult, replyDetailDtoList);
                    }
                }else {
                    commentDetailDto = new CommentDetailDto(comment, comment.getComment(), commentLikeCnt, commentLikeResult, replyDetailDtoList);
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
                .secretMode(commentDto.getSecretMode())
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
    @Operation(summary = "대댓글 최대 10개 조회", description = "대댓글 pk값을 통해서 현재 조회되지 않은 댓글을 최대 10개를 반환하는 API입니다." +
            "\n\n Ex.현재 조회된 대댓글의 최소 Pk값이 25라면 그 전의 PK값인 20,22,23의 댓글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "최대 10개의 대댓글을 반환합니다.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ReplyDetailDto.class)))
    @GetMapping("/reply")
    public ResponseEntity<?> inquiryReply(@Parameter(description = "글 pk값", required = true) @RequestParam("postId") Long postId,
                                          @Parameter(description = "대댓글이 작성된 댓글 pk 값", required = true) @RequestParam("commentId") Long commentId,
                                          @Parameter(description = "현재 조회된 최소 대댓글 pk값", required = true) @RequestParam("replyId") Long replyId,
                                          @Parameter(description = "사용자 email", required = true) @RequestParam("userEmail") String userEmail){
        Optional<Post> post = postService.findSinglePost(postId);
        List<String> anonymityMemberList = anonymityMemberService.findAllUserEmail(post.get());
        List<Reply> replies = replyService.findNextReplies(commentId, replyId);

        List<ReplyDetailDto> replyDtoList = new ArrayList<>();
        for (Reply reply : replies) {
            ReplyDetailDto replyDetailDto = null;
            //비밀 대댓글에 따른 저장 로직
            if (reply.getSecretMode() == Boolean.TRUE) {
                if (userEmail.equals(post.get().getMemberId().getEmail())) {
                    //글쓴이일 시 비밀댓글 상관없이 모두 조회 가능
                    replyDetailDto = new ReplyDetailDto(reply, reply.getReply());
                } else {
                    //글쓴이가 아닐 시 해당 댓글 작성 사용자만 비밀댓글 조회 가능
                    replyDetailDto =
                            (reply.getMemberId().getEmail() == userEmail)
                                    ? new ReplyDetailDto(reply, reply.getReply())
                                    : new ReplyDetailDto(reply, "비밀 댓글입니다.");
                }
            }else {
                replyDetailDto = new ReplyDetailDto(reply, reply.getReply());
            }
        }
        Collections.reverse(replyDtoList);

        return ResponseEntity.ok().body(replyDtoList);
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
                    .secretMode(replyDto.getSecretMode())
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