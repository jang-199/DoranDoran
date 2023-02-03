package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<CommentLike> findCommentLikeList(Long commentId) {
        return commentLikeRepository.findCommentLikeByCommentId(commentId);
    }
}
