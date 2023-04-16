package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReplyService {
    public Integer findReplyCntByComment(Comment comment);
    public List<Reply> findReplyList(Comment comment);
    public void deleteReply(Reply reply);
    public void saveReply(Reply reply);
    public Optional<Reply> findReplyByReplyId(Long replyId);
    public List<Reply> findFirstReplies(Comment comment);
    public List<Reply> findNextReplies(Long commentId, Long replyId);
}
