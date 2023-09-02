package com.dorandoran.doranserver.domain.comment.service.common;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;

import java.util.List;

public interface ReplyCommonService {
    void saveReply(ReplyDto.CreateReply replyDto,
                   Comment comment,
                   Reply buildReply,
                   List<String> anonymityMembers,
                   String userEmail,
                   AnonymityMember anonymityMember);
}
