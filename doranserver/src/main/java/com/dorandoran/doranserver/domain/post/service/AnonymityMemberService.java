package com.dorandoran.doranserver.domain.post.service;

import com.dorandoran.doranserver.domain.post.domain.AnonymityMember;
import com.dorandoran.doranserver.domain.post.domain.Post;

import java.util.List;

public interface AnonymityMemberService {
    public List<String> findAllUserEmail(Post post);
    public void save(AnonymityMember anonymityMember);

    void deletePostByPostId(Post post);

}
