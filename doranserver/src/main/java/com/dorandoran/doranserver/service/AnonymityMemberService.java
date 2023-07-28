package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.AnonymityMember;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;

public interface AnonymityMemberService {
    public List<String> findAllUserEmail(Post post);
    public void save(AnonymityMember anonymityMember);

    void deletePostByPostId(Post post);
}
