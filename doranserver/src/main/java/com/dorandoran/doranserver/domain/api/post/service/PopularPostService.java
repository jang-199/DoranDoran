package com.dorandoran.doranserver.domain.api.post.service;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.PopularPost;
import com.dorandoran.doranserver.domain.api.post.domain.Post;

import java.util.List;

public interface PopularPostService {
    List<Post> findFirstPopularPost(Member member, List<Member> memberBlockListByBlockingMember);

    List<Post> findPopularPost(Long startPost,Member member, List<Member> memberBlockListByBlockingMember);
    void savePopularPost(PopularPost popularPost);
    List<PopularPost> findPopularPostByPost(Post post);
    void deletePopularPost(PopularPost popularPost);
}
