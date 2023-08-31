package com.dorandoran.doranserver.domain.hashtag.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.hashtag.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService{
    private final HashTagRepository hashTagRepository;

    @Override
    public void saveHashTag(HashTag hashTag) {
        hashTagRepository.save(hashTag);
    }

    @Override
    public Boolean duplicateCheckHashTag(String hashTag) {
        Optional<HashTag> findByHashTag = hashTagRepository.findByHashTagName(hashTag);
        return findByHashTag.isEmpty();
    }

    @Override
    public HashTag findByHashTagName(String hashTag) {
        return hashTagRepository.findByHashTagName(hashTag).orElseThrow(() -> new RuntimeException("검색한 해시태그가 없습니다."));
    }

    @Override
    public List<HashTag> findTop5BySearchHashTag(String hashTag) {
        PageRequest of = PageRequest.of(0, 5);
        return hashTagRepository.findTop5BySearchHashTag(of, hashTag);
    }

    @Override
    public List<HashTag> findPopularHashTagTop5() {
        PageRequest of = PageRequest.of(0, 5);
        return hashTagRepository.findPopularHashTagTop5(of);
    }
}
