package com.dorandoran.doranserver.domain.hashtag.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;

import java.util.List;

public interface HashTagService {
    void saveHashTag(HashTag hashTag);

    void saveHashtagList(List<String> hashTagList);

    Boolean duplicateCheckHashTag(String hashTag);
    HashTag findByHashTagName(String hashTag);
    List<HashTag> findTop5BySearchHashTag(String hashTag);
    List<HashTag> findPopularHashTagTop5();
    List<HashTag> findHashtagList(List<String> hashTagList);
}
