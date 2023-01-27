package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("select m from Post m order by m.postId desc")
    List<Post> findFirstPost(PageRequest pageRequest);

    @Query(value = "select m from Post m where m.postId <= :pos order by m.postId desc ")
    List<Post> findPost(@Param("pos") Long pos,PageRequest pageRequest);
}
