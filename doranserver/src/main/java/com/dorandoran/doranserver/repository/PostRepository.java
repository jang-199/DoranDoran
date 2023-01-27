package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
}
