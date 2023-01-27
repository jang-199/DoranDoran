package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {
    Optional<HashTag> findByHashTagName(String hashTag);
}
