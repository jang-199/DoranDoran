package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.repository.CommentRepository;
import com.dorandoran.doranserver.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ReplyServiceImpl replyService;
    private final CommentLikeServiceImpl commentLikeService;

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Integer findCommentAndReplyCntByPostId(Post post) {
        Integer cnt = 0;
        List<Comment> commentCntByPostId = commentRepository.findCommentCntByPostId(post);
        cnt += commentCntByPostId.size();
        for (Comment comment : commentCntByPostId) {
            List<Reply> replyCntByComment = replyRepository.findReplyCntByComment(comment);
            cnt += replyCntByComment.size();
        }
        return cnt;
    }

    @Override
    public List<Comment> findCommentByPost(Post post) {
        return commentRepository.findCommentByPostId(post);
    }

    @Override
    public Optional<Comment> findCommentByCommentId(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public void deleteAllCommentByPost(Optional<Comment> comment, List<CommentLike> commentLikeList, List<Reply> replyList) {
        if (replyList.size() != 0) {
            for (Reply reply : replyList) {
                replyService.deleteReply(reply);
                log.info("{}님의 대댓글 삭제", reply.getMemberId().getNickname());
            }
        }
        //댓글 공감 삭제
        if (commentLikeList.size() != 0) {
            for (CommentLike commentLike : commentLikeList) {
                commentLikeService.deleteCommentLike(commentLike);
                log.info("{}님의 댓글 공감 삭제", commentLike.getMemberId().getNickname());
            }
        }
        //댓글 삭제
        deleteComment(comment.get());
    }
}