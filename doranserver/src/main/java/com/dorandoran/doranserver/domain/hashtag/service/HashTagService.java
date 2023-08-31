package com.dorandoran.doranserver.domain.hashtag.service;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;

import java.util.List;

public interface HashTagService {
    void saveHashTag(HashTag hashTag);
    Boolean duplicateCheckHashTag(String hashTag);
    HashTag findByHashTagName(String hashTag);
    List<HashTag> findTop5BySearchHashTag(String hashTag);
    List<HashTag> findPopularHashTagTop5();
}
