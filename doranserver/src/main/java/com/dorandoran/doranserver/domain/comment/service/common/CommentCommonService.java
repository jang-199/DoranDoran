package com.dorandoran.doranserver.domain.comment.service.common;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.post.domain.Post;

import java.util.List;

public interface CommentCommonService {
    void saveComment(CommentDto.CreateComment createCommentDto,
                     Comment comment,
                     List<Comment> commentByPost,
                     List<PopularPost> popularPostByPost,
                     Post post, String userEmail,
                     Long nextIndex,
                     List<String> anonymityMembers);
}
