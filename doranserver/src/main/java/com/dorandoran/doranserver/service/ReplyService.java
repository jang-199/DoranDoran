package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;

public interface ReplyService {
    public Integer findReplyCntByComment(Comment comment);
}
