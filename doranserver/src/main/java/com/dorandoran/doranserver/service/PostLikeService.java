package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.PostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostLikeService {
    public void savePostLike(PostLike postLike);
    public Integer findLIkeCnt(Post post);
    public Boolean findLikeResult(String email, Post postId);
    public List<PostLike> findByMemberId(String email);
    public void deletePostLike(PostLike postLike);
    public List<PostLike> findByPost(Post post);
    List<PostLike> findFirstMyLikedPosts(String email);
    List<PostLike> findMyLikedPosts(String email, Long position);
    Optional<PostLike> findLikeOne(String email, Post post);
    void checkPostLike(PostDto.LikePost postLikeDto, UserDetails userDetails, Post post, Member member, Optional<PostLike> postLike);
}
