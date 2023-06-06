package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostHash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashRepository extends JpaRepository<PostHash,Long> {
    List<PostHash> findPostHashByPostId(Post post);

    Optional<PostHash> findTopByHashTagIdOrderByPoshHashIdDesc(HashTag hashTag);
}
