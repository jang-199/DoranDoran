package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;

    @Override
    public Integer findReplyCntByComment(Comment comment) {
        List<Reply> replyCntByComment = replyRepository.findReplyCntByComment(comment);
        return replyCntByComment.size();
    }

    @Override
    public List<Reply> findReplyList(Comment comment) {
        return replyRepository.findReplyCntByComment(comment);
    }
}
