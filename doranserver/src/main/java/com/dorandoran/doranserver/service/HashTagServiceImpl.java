package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
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
    public Optional<HashTag> findByHashTagName(String hashTag) {
        return hashTagRepository.findByHashTagName(hashTag);
    }

    @Override
    public List<HashTag> findTop5BySearchHashTag(String hashTag) {
        PageRequest of = PageRequest.of(0, 5);
        return hashTagRepository.findTop5BySearchHashTag(of, hashTag);
    }
}
