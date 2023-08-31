package com.dorandoran.doranserver.domain.notification.service;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.member.domain.LockMember;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;

import java.util.List;

public interface FirebaseService {
    void notifyComment(Member member, Comment content);

    void notifyReply(List<Member> memberList, Reply reply);

    void notifyPostLike(Member member, Post post);

    void notifyCommentLike(Member member, Comment comment);

    void notifyBlockedMember(LockMember lockMember);
}
