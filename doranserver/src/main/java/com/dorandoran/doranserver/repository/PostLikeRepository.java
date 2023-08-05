package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostLike;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    List<PostLike> findByPostId(@Param("post") Post post);
    @Query("select pl from PostLike pl where pl.postId = :post and pl.checkDelete = false")
    List<PostLike> findUnDeletedByPost(@Param("post") Post post);
    List<PostLike> findByMemberId_Email(String email);
    @Query("select p from PostLike p " +
            "inner join Member m on m.memberId=p.memberId.memberId " +
            "where m.email=:email and p.postId=:postId")
    Optional<PostLike> findLikeResult(@Param("email") String email, @Param("postId") Post postId);

    @Query("select p from PostLike p " +
            "where p.memberId.email = :email and p.checkDelete = false " +
            "order by p.postLikeId desc ")
    List<PostLike> findFirstMyLikedPosts(@Param("email") String email, PageRequest pageRequest);

    @Query("select p from PostLike p " +
            "where p.memberId.email = :email and p.postLikeId <= :pos and p.checkDelete = false " +
            "order by p.postLikeId desc ")
    List<PostLike> findMyLikedPosts(@Param("email") String email,
                                    @Param("pos") Long position,
                                    PageRequest pageRequest);
}
