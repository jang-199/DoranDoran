package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
}
