package com.dorandoran.doranserver.domain.api.hashtag.service;

import com.dorandoran.doranserver.domain.api.hashtag.domain.HashTag;

import java.util.List;

public interface HashTagService {
    void saveHashTag(HashTag hashTag);
    Boolean duplicateCheckHashTag(String hashTag);
    HashTag findByHashTagName(String hashTag);
    List<HashTag> findTop5BySearchHashTag(String hashTag);
    List<HashTag> findPopularHashTagTop5();
}
