package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.CommentLike;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.domain.Reply;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    void saveComment(Comment comment);

    Integer findCommentAndReplyCntByPostId(List<Comment> commentByPostList, List<Reply> replyCntByCommentList);

    List<Integer> findCommentAndReplyCntByPostIdByList(List<Post> post);
    public List<Comment> findCommentByPost(Post post);
    public Comment findCommentByCommentId(Long commentId);
    public void deleteComment(Comment comment);
    public void deleteAllCommentByPost(Optional<Comment> comment, List<CommentLike> commentLikeList, List<Reply> replyList);
    public List<Comment> findFirstComments(Post post);
    public List<Comment> findFirstCommentsFetchMember(Post post);
    public List<Comment> findNextComments(Long postId, Long commentId);
    void checkSecretComment(CommentDto.ReadCommentResponse commentDetailDto, Post post, Comment comment, Member userMember);
    void checkCommentAnonymityMember(List<String> anonymityMemberList, Comment comment, CommentDto.ReadCommentResponse commentDetailDto);
    List<Comment> findBlockedComment(Integer page);
    List<Comment> findBlockedCommentDetail(Post post);
    void setUnLocked(Comment comment);
    Comment findFetchMember(Long commentId);
    void setCheckDelete(Comment comment);
    Boolean checkExistAndDelete(List<Comment> commentList);
}
