package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.PostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberBlockList;
import com.dorandoran.doranserver.entity.Post;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PostService {
    void savePost(Post post);

    void saveMemberPost(Post post, PostDto.CreatePost postDto) throws IOException;

    List<Post> findFirstPost(List<MemberBlockList> memberBlockLists);

    List<Post> findPost(Long startPost,List<MemberBlockList> memberBlockLists);
    Post findSinglePost(Long postId);
    void deletePost(Post post);
    List<Post> findFirstClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,List<MemberBlockList> memberBlockListByBlockingMember);
    List<Post> findClosePost(Double Slatitude,Double Llatitude, Double Slongitude, Double Llongitude,Long startPost,List<MemberBlockList> memberBlockListByBlockingMember);
    List<Post> findFirstMyPost(Member member);
    List<Post> findMyPost(Member member, Long startPost);

    List<Post> findBlockedPost(Integer page);

    Post findFetchMember(Long postId);
    void setUnLocked(Post post);

    void setPostPic(PostDto.CreatePost postDto, Post post) throws IOException;
}
