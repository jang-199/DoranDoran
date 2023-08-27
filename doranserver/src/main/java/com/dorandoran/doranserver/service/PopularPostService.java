package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;

public interface PopularPostService {
    List<Post> findFirstPopularPost(Member member, List<Member> memberBlockListByBlockingMember);

    List<Post> findPopularPost(Long startPost,Member member, List<Member> memberBlockListByBlockingMember);
    void savePopularPost(PopularPost popularPost);
    List<PopularPost> findPopularPostByPost(Post post);
    void deletePopularPost(PopularPost popularPost);
}
