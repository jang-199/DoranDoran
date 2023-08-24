package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void saveHashtagList(List<String> hashTagList){
        for (String hashTag : hashTagList) {
            if (duplicateCheckHashTag(hashTag)) {
                HashTag buildHashTag = HashTag.builder()
                        .hashTagName(hashTag)
                        .hashTagCount(1L)
                        .build();
                saveHashTag(buildHashTag);
            } else {
                HashTag byHashTagName = findByHashTagName(hashTag);
                Long hashTagCount = byHashTagName.getHashTagCount();
                byHashTagName.setHashTagCount(hashTagCount + 1);
            }
        }
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

    @Override
    public List<HashTag> findHashtagList(List<String> hashTagList) {
        return hashTagRepository.findHashTagList(hashTagList);
    }
}
