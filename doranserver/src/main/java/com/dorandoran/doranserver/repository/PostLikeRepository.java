package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
}
