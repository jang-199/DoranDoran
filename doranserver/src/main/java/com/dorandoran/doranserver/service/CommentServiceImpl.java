package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.repository.CommentRepository;
import com.dorandoran.doranserver.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

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
}
