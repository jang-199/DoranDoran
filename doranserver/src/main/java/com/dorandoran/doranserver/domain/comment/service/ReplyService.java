package com.dorandoran.doranserver.domain.comment.service;

import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.comment.domain.Reply;

import java.util.List;
import java.util.Optional;

public interface ReplyService {
    Integer findReplyCntByComment(Comment comment);

    List<Reply> findReplyByCommentList(List<Comment> commentByPostList);

    List<Reply> findReplyList(Comment comment);
    void deleteReply(Reply reply);
    void saveReply(Reply reply);
    Reply findReplyByReplyId(Long replyId);
    List<Reply> findFirstReplies(Comment comment);
    List<Reply> findFirstRepliesFetchMember(Comment comment);
    List<Reply> findNextReplies(Long commentId, Long replyId);
    void checkSecretReply(ReplyDto.ReadReplyResponse replyDetailDto, Post post, Reply reply, Member userMember);
    void checkReplyAnonymityMember(List<String> anonymityMemberList, Reply reply, ReplyDto.ReadReplyResponse replyDetailDto);
    List<Member> findReplyMemberByComment(Comment comment);
    List<Reply> findBlockedReply(Integer page);
    List<Reply> findBlockedReplyDetail(Comment comment);
    void setUnLocked(Reply reply);
    Reply findFetchMember(Long replyId);
    List<Reply> findTest(List<Comment> commentList);
    void setCheckDelete(Reply reply);
    Boolean checkExistAndDelete(List<Reply> replyList);
    List<Reply> findRankRepliesByComments(List<Comment> comments);
}
