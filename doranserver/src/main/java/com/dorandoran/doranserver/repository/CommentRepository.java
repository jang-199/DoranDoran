package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
