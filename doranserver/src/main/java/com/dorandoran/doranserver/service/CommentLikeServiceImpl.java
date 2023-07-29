package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.CommentDto;
import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.CommentLike;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeServiceImpl implements CommentLikeService{
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public Integer findCommentLikeCnt(Comment comment) {
        List<CommentLike> commentLikeByCommentId = commentLikeRepository.findUnDeletedByCommentId(comment);
        return commentLikeByCommentId.size();
    }

    @Override
    public List<CommentLike> findCommentLikeListByCommentId(Comment comment) {
        return commentLikeRepository.findCommentLikeByCommentId(comment);
    }

    @Override
    public List<CommentLike> findByCommentId(Comment comment) {
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

    @Override
    public Boolean findCommentLikeResult(String userEmail, Comment commentId) {
        if(commentLikeRepository.findCommentLikeResult(userEmail,commentId).isPresent()){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Optional<CommentLike> findCommentLikeOne(String userEmail, Comment comment) {
        return commentLikeRepository.findCommentLikeResult(userEmail,comment);
    }

    @Override
    @Transactional
    public void checkCommentLike(CommentDto.LikeComment commentLikeDto, UserDetails userDetails, Comment comment, Member member, Optional<CommentLike> commentLike) {
        if (commentLike.isPresent()){
            if (commentLike.get().getCheckDelete().equals(Boolean.TRUE)){
                commentLike.get().restore();
            }else {
                commentLike.get().delete();
                log.info("{} 글의 {} 댓글 공감 취소", commentLikeDto.getPostId(), commentLike.get().getCommentId().getCommentId());
            }
        }else {
            CommentLike commentLikeBuild = CommentLike.builder()
                    .commentId(comment)
                    .memberId(member)
                    .checkDelete(Boolean.FALSE)
                    .build();
            saveCommentLike(commentLikeBuild);
        }
    }
}
