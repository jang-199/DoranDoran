package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HashTagService {
    public void saveHashTag(HashTag hashTag);
    public Boolean duplicateCheckHashTag(String hashTag);
    public Optional<HashTag> findByHashTagName(String hashTag);
    public List<HashTag> findTop5BySearchHashTag(String hashTag);
    public List<HashTag> findByHashTagNameList(List<String> hashTag);
    public List<HashTag> findPopularHashTagTop5();
}
