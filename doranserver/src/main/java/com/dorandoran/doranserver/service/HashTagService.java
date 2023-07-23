package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HashTagService {
    void saveHashTag(HashTag hashTag);
    Boolean duplicateCheckHashTag(String hashTag);
    HashTag findByHashTagName(String hashTag);
    List<HashTag> findTop5BySearchHashTag(String hashTag);
    List<HashTag> findPopularHashTagTop5();
}
