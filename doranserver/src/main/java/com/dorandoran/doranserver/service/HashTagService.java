package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.HashTag;

import java.util.Optional;

public interface HashTagService {
    public void saveHashTag(HashTag hashTag);
    public Boolean duplicateCheckHashTag(String hashTag);
    public Optional<HashTag> findByHashTagName(String hashTag);
}
