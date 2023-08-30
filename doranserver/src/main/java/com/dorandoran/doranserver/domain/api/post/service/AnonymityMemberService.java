package com.dorandoran.doranserver.domain.api.post.service;

import com.dorandoran.doranserver.domain.api.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.api.post.domain.Post;

import java.util.List;

public interface AnonymityMemberService {
    public List<String> findAllUserEmail(Post post);
    public void save(AnonymityMember anonymityMember);

    void deletePostByPostId(Post post);
}
