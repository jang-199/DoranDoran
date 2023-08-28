package com.dorandoran.doranserver.domain.api.post.service;

import com.dorandoran.doranserver.domain.api.post.dto.PostDto;
import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.domain.api.post.domain.Post;
import com.dorandoran.doranserver.domain.api.post.domain.PostLike;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface PostLikeService {
    public void savePostLike(PostLike postLike);
    public Integer findLIkeCnt(Post post);
    List<Integer> findLIkeCntByPostList(List<Post> post);
    public Boolean findLikeResult(String email, Post postId);
    List<Boolean> findLikeResultByPostList(String email, List<Post> postList);
    public List<PostLike> findByMemberId(String email);
    public void deletePostLike(PostLike postLike);
    public List<PostLike> findByPost(Post post);
    List<PostLike> findFirstMyLikedPosts(String email, List<Member> memberBlockListByBlockingMember);
    List<PostLike> findMyLikedPosts(String email, Long position, List<Member> memberBlockListByBlockingMember);
    Optional<PostLike> findLikeOne(String email, Post post);
    void checkPostLike(PostDto.LikePost postLikeDto, UserDetails userDetails, Post post, Member member, Optional<PostLike> postLike);
}
