package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    public void savePost(Post post);

    public List<Post> findFirstPost(List<MemberBlockList> memberBlockLists);

    public List<Post> findPost(Long startPost,List<MemberBlockList> memberBlockLists);
    public Post findSinglePost(Long postId);
    public void deletePost(Post post);

    public List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,List<MemberBlockList> memberBlockListByBlockingMember);

    public List<Post> findClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,Long startPost,List<MemberBlockList> memberBlockListByBlockingMember);

    List<Post> findFirstMyPost(Member member);
    List<Post> findMyPost(Member member, Long startPost);
}
