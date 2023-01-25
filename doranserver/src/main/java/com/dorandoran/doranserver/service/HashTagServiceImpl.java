package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Optional<HashTag> findByHashTagName(String hashTag) {
        Optional<HashTag> byHashTagName = hashTagRepository.findByHashTagName(hashTag);
        return byHashTagName;
    }
}
