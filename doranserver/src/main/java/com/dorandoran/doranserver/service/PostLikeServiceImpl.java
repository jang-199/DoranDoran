package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PostLike;
import com.dorandoran.doranserver.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;

    @Override
    public void savePostLike(PostLike postLike) {
        postLikeRepository.save(postLike);
    }

    @Override
    public Optional<PostLike> findByMemberId(String email) {
        return postLikeRepository.findByMemberId_Email(email);
    }

    @Override
    public void deletePostLike(String email) {
        postLikeRepository.deleteByMemberId_Email(email);
    }
}
