package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    List<PostLike> findByPostId(Post post);
    Optional<List<PostLike>> findByMemberId_Email(String email);
    @Query("select p from PostLike p where p.memberId = :memberId and p.postId = :postId")
    Optional<PostLike> findLikeResult(@Param("memberId") Member memberId, @Param("postId") Post postId);

}
