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

    @Query("select count(pl) from PostLike pl where pl.checkDelete = false and pl.postId in :postList group by pl.postId")
    List<Integer> findPostLikeByPostList(@Param("postList") List<Post> postList);
    List<PostLike> findByMemberId_Email(String email);
    @Query("select p from PostLike p " +
            "inner join Member m on m.memberId=p.memberId.memberId " +
            "where m.email=:email and p.postId=:postId ")
    Optional<PostLike> findLikeResult(@Param("email") String email, @Param("postId") Post postId);

    @Query("select p from PostLike p " +
            "inner join Member m on m=p.memberId and m.email=:email and p.postId in :postList " +
            "order by p.postId.postId desc ")
    List<PostLike> findLikeResultByPostList(@Param("email") String email, @Param("postList") List<Post> postList);

    @Query("select p from PostLike p " +
            "where p.memberId.email = :email and p.checkDelete = false " +
            "order by p.postLikeId desc ")
    List<PostLike> findFirstMyLikedPosts(@Param("email") String email, PageRequest pageRequest);
    @Query("select p from PostLike p " +
            "where p.memberId.email = :email and p.checkDelete = false and p.memberId not in :members " +
            "order by p.postLikeId desc ")
    List<PostLike> findFirstMyLikedPostsWithoutBlockLists(@Param("email") String email,
                                                          @Param("members") List<Member> members,
                                                          PageRequest pageRequest);

    @Query("select p from PostLike p " +
            "where p.memberId.email = :email and p.postLikeId <= :pos and p.checkDelete = false " +
            "order by p.postLikeId desc ")
    List<PostLike> findMyLikedPosts(@Param("email") String email,
                                    @Param("pos") Long position,
                                    PageRequest pageRequest);
    @Query("select p from PostLike p " +
            "where p.memberId.email = :email and p.postLikeId <= :pos and p.checkDelete = false and p.memberId not in :members " +
            "order by p.postLikeId desc ")
    List<PostLike> findMyLikedPostsWithoutBlockLists(@Param("email") String email,
                                                     @Param("pos") Long position,
                                                     @Param("members") List<Member> members,
                                                     PageRequest pageRequest);
}
