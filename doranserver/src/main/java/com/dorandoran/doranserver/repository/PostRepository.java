package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
