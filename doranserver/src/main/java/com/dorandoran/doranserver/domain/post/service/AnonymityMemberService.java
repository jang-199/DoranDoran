package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.Post;

import java.util.List;

public interface AnonymityMemberService {
    List<String> findAllUserEmail(Post post);
    void save(AnonymityMember anonymityMember);
    void checkAndSave(Boolean anonymityCheck, List<String> anonymityMembers, String userEmails, AnonymityMember anonymityMember);

    void deletePostByPostId(Post post);

}
