package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;

import java.util.List;
import java.util.Optional;

public interface ReplyService {
    Integer findReplyCntByComment(Comment comment);
    List<Reply> findReplyList(Comment comment);
    void deleteReply(Reply reply);
    void saveReply(Reply reply);
    Optional<Reply> findReplyByReplyId(Long replyId);
    List<Reply> findFirstReplies(Comment comment);
    List<Reply> findFirstRepliesFetchMember(Comment comment);
    List<Reply> findNextReplies(Long commentId, Long replyId);
}
