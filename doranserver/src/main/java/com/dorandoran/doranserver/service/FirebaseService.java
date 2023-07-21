package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.*;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.io.IOException;
import java.util.List;

public interface FirebaseService {
    void notifyComment(Member member, Comment content);

    void notifyReply(List<Member> memberList, Reply reply, Member writeMember);

    void notifyPostLike(Member member, Post post);

    void notifyCommentLike(Member member, Comment comment);

    void notifyBlockedMember(LockMember lockMember);
}
