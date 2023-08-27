package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    void savePost(Post post);

    List<Post> findFirstPost(Member member, List<Member> memberBlockLists);

    List<Post> findPost(Long startPost, Member member, List<Member> memberBlockLists);
    Post findSinglePost(Long postId);
    void deletePost(Post post);
    List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,List<Member> memberBlockListByBlockingMember);
    List<Post> findClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,Long startPost,List<Member> memberBlockListByBlockingMember);
    List<Post> findFirstMyPost(Member member);
    List<Post> findMyPost(Member member, Long startPost);

    List<Post> findBlockedPost(Integer page);

    Post findFetchMember(Long postId);
    void setUnLocked(Post post);
}
