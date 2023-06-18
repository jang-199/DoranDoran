package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostHash;
import com.dorandoran.doranserver.repository.PostHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    public List<PostHash> findPostHash(Post post) {
        return postHashRepository.findPostHashByPostId(post);
    }

    @Override
    public void deletePostHash(PostHash postHash) {
        postHashRepository.delete(postHash);
    }

    @Override
    public Optional<PostHash> findTopOfPostHash(HashTag hashTag) {
        return postHashRepository.findTopByHashTagIdOrderByPoshHashIdDesc(hashTag);
    }

    @Override
    public List<PostHash> inquiryFirstPostHash(HashTag hashTag) {
        PageRequest of = PageRequest.of(0, 20);
        return postHashRepository.findFirstPostHash(hashTag, of);
    }

    @Override
    public List<PostHash> inquiryPostHash(HashTag hashTag, Long postCnt) {
        PageRequest of = PageRequest.of(0, 20);
        return postHashRepository.findPostHash(hashTag, postCnt, of);
    }


}
