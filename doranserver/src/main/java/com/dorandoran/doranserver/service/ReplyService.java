package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Reply;

import java.util.List;

public interface ReplyService {
    public Integer findReplyCntByComment(Comment comment);
    public List<Reply> findReplyList(Comment comment);
    public List<String> findReplyContents(Comment comment);
    public void deleteReply(Reply reply);
}
