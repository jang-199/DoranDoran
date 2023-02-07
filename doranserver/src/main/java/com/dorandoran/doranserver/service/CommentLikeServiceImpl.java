package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService{
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public Integer findCommentLikeCnt(Long commentId) {
        List<CommentLike> commentLikeByCommentId = commentLikeRepository.findCommentLikeByCommentId(commentId);
        return commentLikeByCommentId.size();
    }

    @Override
    public List<CommentLike> findCommentLikeListByCommentId(Long commentId) {
        return commentLikeRepository.findCommentLikeByCommentId(commentId);
    }

    @Override
    public Optional<List<CommentLike>> findByCommentId(Comment comment) {
        return commentLikeRepository.findByCommentId(comment);
    }

    @Override
    public void saveCommentLike(CommentLike commentLike) {
        commentLikeRepository.save(commentLike);
    }

    @Override
    public void deleteCommentLike(CommentLike commentLike) {
        commentLikeRepository.delete(commentLike);
    }
}
