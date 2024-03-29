package com.dorandoran.doranserver.domain.post.repository;

import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.post.domain.PostLike;
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

    @Query("select pl from PostLike pl " +
            "join fetch pl.postId " +
            "where pl.checkDelete = false and pl.postId in :postList " +
            "order by pl.postId.postId desc ")
    List<PostLike> findPostLikeByPostList(@Param("postList") List<Post> postList);
    List<PostLike> findByMemberId_Email(String email);
    @Query("select p from PostLike p " +
            "inner join Member m on m.memberId=p.memberId.memberId " +
            "where m.email=:email and p.postId=:postId ")
    Optional<PostLike> findLikeResult(@Param("email") String email, @Param("postId") Post postId);

    @Query("select p from PostLike p " +
            "inner join Member m on m=p.memberId and m.email=:email and p.postId in :postList " +
            "order by p.postId.postId desc ")
    List<PostLike> findLikeResultByPostList(@Param("email") String email, @Param("postList") List<Post> postList);

    @Query("select p.postId from PostLike p " +
            "where p.memberId.email = :email and p.checkDelete = false and p.postId.isLocked = false and ((p.postId.memberId.email = :email and p.postId.forMe = true ) or (p.postId.forMe = false))" +
            "order by p.postLikeId desc ")
    List<Post> findFirstMyLikedPosts(@Param("email") String email, PageRequest pageRequest);
    @Query("select p.postId from PostLike p " +
            "where p.memberId.email = :email and p.checkDelete = false and p.memberId not in :members and p.postId.isLocked = false and ((p.postId.memberId.email = :email and p.postId.forMe = true ) or (p.postId.forMe = false)) " +
            "order by p.postLikeId desc ")
    List<Post> findFirstMyLikedPostsWithoutBlockLists(@Param("email") String email,
                                                          @Param("members") List<Member> members,
                                                          PageRequest pageRequest);

    @Query("select p.postId from PostLike p " +
            "where p.memberId.email = :email and p.postLikeId <= :pos and p.checkDelete = false and p.postId.isLocked = false and ((p.postId.memberId.email = :email and p.postId.forMe = true ) or (p.postId.forMe = false)) " +
            "order by p.postLikeId desc ")
    List<Post> findMyLikedPosts(@Param("email") String email,
                                    @Param("pos") Long position,
                                    PageRequest pageRequest);
    @Query("select p.postId from PostLike p " +
            "where p.memberId.email = :email and p.postLikeId <= :pos and p.checkDelete = false and p.memberId not in :members and p.postId.isLocked = false and ((p.postId.memberId.email = :email and p.postId.forMe = true ) or (p.postId.forMe = false)) " +
            "order by p.postLikeId desc ")
    List<Post> findMyLikedPostsWithoutBlockLists(@Param("email") String email,
                                                     @Param("pos") Long position,
                                                     @Param("members") List<Member> members,
                                                     PageRequest pageRequest);
}
