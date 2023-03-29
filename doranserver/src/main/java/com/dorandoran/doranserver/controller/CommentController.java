package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.CommentDeleteDto;
import com.dorandoran.doranserver.dto.CommentDto;
import com.dorandoran.doranserver.dto.CommentLikeDto;
import com.dorandoran.doranserver.dto.ReplyDto;
import com.dorandoran.doranserver.entity.*;
import com.dorandoran.doranserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @PostMapping("/comment")
    ResponseEntity<?> comment(@RequestBody CommentDto commentDto) {
        Optional<Member> member = memberService.findByEmail(commentDto.getEmail());
        Optional<Post> post = postService.findSinglePost(commentDto.getPostId());
        log.info("사용자 {}의 댓글 작성", commentDto.getEmail());
        Comment comment = Comment.builder()
                .comment(commentDto.getComment())
                .commentTime(LocalDateTime.now())
                .postId(post.get())
                .memberId(member.get())
                .anonymity(commentDto.getAnonymity())
                .build();
        commentService.saveComment(comment);
        Optional<Post> singlePost = postService.findSinglePost(commentDto.getPostId());
        if (singlePost.isPresent()) {
            List<Comment> commentByPost = commentService.findCommentByPost(singlePost.get());
            if (commentByPost.size() >= 10) {
                PopularPost build = PopularPost.builder().postId(singlePost.get()).build();
                popularPostService.savePopularPost(build);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 대댓글 삭제 -> 댓글 공감 삭제 -> 댓글 삭제
     * 삭제하려는 사용자 email과 작성한 댓글의 사용자 email이 다를 경우 bad request
     * @param commentDeleteDto
     * @return
     */
    @PostMapping("/comment-delete")
    public ResponseEntity<?> deleteComment(@RequestBody CommentDeleteDto commentDeleteDto){
        Optional<Comment> comment = commentService.findCommentByCommentId(commentDeleteDto.getCommentId());
        List<CommentLike> commentLikeList = commentLikeService.findByCommentId(comment.get());
        List<Reply> replyList = replyService.findReplyList(comment.get());
        if (comment.get().getMemberId().getEmail().equals(commentDeleteDto.getUserEmail())) {
            //대댓글 삭제
            commentService.deleteAllCommentByPost(comment, commentLikeList, replyList);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
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

    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody ReplyDto replyDto) {
        Optional<Comment> comment = commentService.findCommentByCommentId(replyDto.getCommentId());
        Optional<Member> member = memberService.findByEmail(replyDto.getUserEmail());

        if (comment.isPresent() && member.isPresent()) {
            Reply buildReply = Reply.builder()
                    .reply(replyDto.getReply())
                    .ReplyTime(LocalDateTime.now())
                    .anonymity(replyDto.getAnonymity())
                    .commentId(comment.get())
                    .memberId(member.get())
                    .build();

            replyService.saveReply(buildReply);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}