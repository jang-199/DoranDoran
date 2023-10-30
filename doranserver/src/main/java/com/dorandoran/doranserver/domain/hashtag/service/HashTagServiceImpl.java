package com.dorandoran.doranserver.domain.hashtag.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.hashtag.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public void updateHashtagCountOrSaveHashtagList(List<String> hashTagNameList) {
        List<HashTag> isExistHashtagList = findHashtagList(hashTagNameList);
        updateHashtagCount(isExistHashtagList);

        List<String> isNotExistHashTagNameList = findNotExistHashTagNameList(hashTagNameList, isExistHashtagList);
        saveHashtagListByName(isNotExistHashTagNameList);
    }

    private void updateHashtagCount(List<HashTag> isExistHashtagNameList) {
        isExistHashtagNameList.iterator().forEachRemaining(HashTag::addHashtagCount);
    }


    private List<String> findNotExistHashTagNameList(List<String> hashTagNameList, List<HashTag> isExistHashtagList) {
        ArrayList<String> isNotExistHashTagList = new ArrayList<>();
        List<String> isExistHashtagNameList = isExistHashtagList.stream().map(HashTag::getHashTagName).toList();

        for (String hashTagName : hashTagNameList) {
            if (!isExistHashtagNameList.contains(hashTagName)){
                isNotExistHashTagList.add(hashTagName);
            }
        }

        return isNotExistHashTagList;
    }

    @Override
    @Transactional
    public void saveHashtagListByName(List<String> hashTagNameList) {
        for (String hashTag : hashTagNameList) {
            HashTag saveHashtag = new HashTag(hashTag);

            saveHashTag(saveHashtag);
        }
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
