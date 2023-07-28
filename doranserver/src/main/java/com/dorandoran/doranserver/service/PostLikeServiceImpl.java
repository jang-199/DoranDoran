package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.dto.PostDto;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import com.dorandoran.doranserver.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final FirebaseService firebaseService;

    @Override
    public void savePostLike(PostLike postLike) {
        postLikeRepository.save(postLike);
    }

    @Override
    public Integer findLIkeCnt(Post post) {
        List<PostLike> byPostId = postLikeRepository.findByPostId(post);
        return byPostId.size();
    }

    @Override
    public Boolean findLikeResult(String email, Post postId) {
        Optional<PostLike> likeResult = postLikeRepository.findLikeResult(email, postId);
        if (likeResult.isPresent()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<PostLike> findByMemberId(String email) {
        return postLikeRepository.findByMemberId_Email(email);
    }

    @Override
    public void deletePostLike(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    @Override
    public List<PostLike> findByPost(Post post) {
        return postLikeRepository.findByPostId(post);
    }

    @Override
    public List<PostLike> findFirstMyLikedPosts(String email) {
        PageRequest of = PageRequest.of(0, 20);
        return postLikeRepository.findFirstMyLikedPosts(email, of);
    }

    @Override
    public List<PostLike> findMyLikedPosts(String email, Long position) {
        PageRequest of = PageRequest.of(0, 20);
        return postLikeRepository.findMyLikedPosts(email,position,of);
    }

    @Override
    public Optional<PostLike> findLikeOne(String email, Post post) {
        return postLikeRepository.findLikeResult(email, post);
    }

    @Override
    @Transactional
    public void checkPostLike(PostDto.LikePost postLikeDto, UserDetails userDetails, Post post, Member member, Optional<PostLike> postLike) {
        if (findLikeResult(userDetails.getUsername(), post)){
            if (postLike.get().getCheckDelete().equals(Boolean.TRUE)){
                postLike.get().restore();
            }else {
                postLike.get().delete();
                log.info("{} 글의 공감 취소", postLikeDto.getPostId());
            }
        }else {
            PostLike postLikeBuild = PostLike.builder()
                    .postId(post)
                    .memberId(member)
                    .build();
            savePostLike(postLikeBuild);
            firebaseService.notifyPostLike(post.getMemberId(), post);
        }
    }

}
