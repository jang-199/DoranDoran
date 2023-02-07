package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostHash;
import com.dorandoran.doranserver.repository.PostHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostHashServiceImpl implements PostHashService{
    private final PostHashRepository postHashRepository;

    @Override
    public void savePostHash(PostHash postHash) {
        postHashRepository.save(postHash);
    }

    @Override
    public Optional<List<PostHash>> findPostHash(Long postId) {
        return postHashRepository.findPostHashByPostId(postId);
    }
}
