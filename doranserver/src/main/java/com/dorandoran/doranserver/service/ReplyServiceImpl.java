package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import com.dorandoran.doranserver.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void deleteReply(Reply reply) {
        replyRepository.delete(reply);
    }

    @Override
    public void saveReply(Reply reply) {
        replyRepository.save(reply);
    }

    @Override
    public Optional<Reply> findReplyByReplyId(Long replyId) {
        return replyRepository.findById(replyId);
    }

    @Override
    public List<Reply> findFirstReplies(Comment comment) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findFirstReplies(comment, of);
    }

    @Override
    public List<Reply> findFirstRepliesFetchMember(Comment comment) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findFirstRepliesFetchMember(comment, of);
    }

    @Override
    public List<Reply> findNextReplies(Long commentId, Long replyId) {
        PageRequest of = PageRequest.of(0, 10);
        return replyRepository.findNextReplies(commentId, replyId, of);
    }
}
