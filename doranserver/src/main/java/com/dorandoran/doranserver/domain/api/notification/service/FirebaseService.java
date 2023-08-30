package com.dorandoran.doranserver.domain.api.notification.service;

import com.dorandoran.doranserver.domain.api.comment.domain.Comment;
import com.dorandoran.doranserver.domain.api.comment.domain.Reply;
import com.dorandoran.doranserver.domain.api.member.domain.LockMember;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;

import java.util.List;

public interface FirebaseService {
    void notifyComment(Member member, Comment content);

    void notifyReply(List<Member> memberList, Reply reply);

    void notifyPostLike(Member member, Post post);

    void notifyCommentLike(Member member, Comment comment);

    void notifyBlockedMember(LockMember lockMember);
}
