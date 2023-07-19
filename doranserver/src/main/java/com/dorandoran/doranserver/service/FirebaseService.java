package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.io.IOException;
import java.util.List;

public interface FirebaseService {
    void notifyComment(Member member, Comment content);

    void notifyReply(List<Member> memberList, Comment comment, Reply reply);

    void notifyPostLike(Member member, Post post);

    void notifyCommentLike(Member member, Comment comment);

    void notifyBlockedMember(String firebaseToken);
}
