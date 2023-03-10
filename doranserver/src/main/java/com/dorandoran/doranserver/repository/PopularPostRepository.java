package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopularPostRepository extends JpaRepository<PopularPost, Long> {
    @Query("select p from PopularPost p order by p.id desc")
    List<PopularPost> findFirstPopularPost(PageRequest pageRequest);

    @Query("select p from PopularPost p where p.id <= :pos order by p.id desc")
    List<PopularPost> findPopularPost(@Param("pos") Long pos, PageRequest pageRequest);

    List<PopularPost> findByPostId(Post post);
}
