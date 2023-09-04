package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.comment.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeServiceImpl implements CommentLikeService{
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public HashMap<Long, Long> findCommentLikeCnt(List<Comment> commentList) {
        List<CommentLike> commentLikeList = commentLikeRepository.findUnDeletedByCommentId(commentList);
        HashMap<Long, Long> commntLikeHashMap = new HashMap<>();

        for (Comment comment : commentList) {
            Long count = commentLikeList.stream().filter(commentLike -> commentLike.getCommentId().equals(comment)).count();
            commntLikeHashMap.put(comment.getCommentId(), count);
        }
        return commntLikeHashMap;
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
    public HashMap<Long, Boolean> findCommentLikeResult(String userEmail, List<Comment> commentList) {
        List<CommentLike> commentLikeResultList = commentLikeRepository.findCommentList(userEmail, commentList);
        HashMap<Long, Boolean> commentLikeResultHashMap = new HashMap<>();

        int position = 0;
        for (Comment comment : commentList) {
            if (commentLikeResultList.isEmpty()){
                commentLikeResultHashMap.put(comment.getCommentId(), Boolean.FALSE);
                continue;
            }

            if (comment.equals(commentLikeResultList.get(position).getCommentId())){
                if (commentLikeResultList.size()-1 != position) {
                    position++;
                }

                commentLikeResultHashMap.put(comment.getCommentId(),Boolean.TRUE);
            }else {
                commentLikeResultHashMap.put(comment.getCommentId(), Boolean.FALSE);
            }
        }

        return commentLikeResultHashMap;
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
