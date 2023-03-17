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
    List<PostLike> findByMemberId_Email(String email);
    @Query("select p from PostLike p inner join Member m on m.memberId=p.memberId.memberId where m.email=:email and p.postId=:postId")
    Optional<PostLike> findLikeResult(@Param("email") String email, @Param("postId") Post postId);
}
