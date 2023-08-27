package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PopularPost;
import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopularPostRepository extends JpaRepository<PopularPost, Long> {
    @Query("select p.postId " +
            "from PopularPost p " +
            "join fetch p.postId.memberId " +
            "where p.postId.isLocked = false and (p.postId.memberId = :member and p.postId.forMe = true ) or (p.postId.forMe = false) " +
            "order by p.id desc")
    List<Post> findFirstPopularPost(@Param("member") Member member, PageRequest pageRequest);

    @Query("select p.postId " +
            "from PopularPost p " +
            "join fetch p.postId.memberId " +
            "where p.postId.isLocked = false and p.postId.memberId not in :blockLists and (p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false) " +
            "order by p.id desc")
    List<Post> findFirstPopularPostWithoutBockLists(@Param("member") Member member, @Param("blockLists") List<Member> blockLists, PageRequest pageRequest);

    @Query("select p.postId from PopularPost p " +
            "join fetch p.postId.memberId " +
            "where p.id <= :pos and p.postId.isLocked = false and ((p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false)) " +
            "order by p.id desc")
    List<Post> findPopularPost(@Param("pos") Long pos,@Param("member") Member member, PageRequest pageRequest);

    @Query("select p " +
            "from PopularPost p " +
            "join fetch p.postId.memberId " +
            "where p.id <= :pos and p.postId.isLocked = false and p.postId.memberId NOT in :blockLists and (p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false) " +
            "order by p.id desc")
    List<Post> findPopularPostWithoutBlockLists(@Param("pos") Long pos, @Param("blockLists") List<Member> blockLists, PageRequest pageRequest);

    List<PopularPost> findByPostId(Post post);
}
