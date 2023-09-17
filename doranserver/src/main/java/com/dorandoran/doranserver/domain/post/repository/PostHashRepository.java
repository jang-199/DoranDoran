package com.dorandoran.doranserver.domain.post.repository;

import com.dorandoran.doranserver.domain.hashtag.domain.HashTag;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.hashtag.domain.PostHash;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostHashRepository extends JpaRepository<PostHash,Long> {

    @Query("select ph from PostHash ph join fetch ph.hashTagId where ph.postId in :postId")
    List<PostHash> findAllByPostId(@Param("postId") List<Post> postId);

    @Query("select ph from PostHash ph join fetch ph.hashTagId where ph.postId = :post")
    List<PostHash> findPostHashByPostId(@Param("post") Post post);


    @Query(value = "SELECT ranking.post_id " +
            "FROM ( " +
            "    SELECT ph.*, RANK() OVER (PARTITION BY ph.hash_tag ORDER BY ph.post_hash_id DESC) AS rn " +
            "    FROM post_hash AS ph " +
            "    JOIN post AS p ON ph.post_id = p.post_id " +
            "    WHERE ph.hash_tag IN :hashTagId AND p.is_locked = FALSE AND ((p.member_id = :memberId and p.for_me = true) or (p.for_me = false)) and p.member_id not in :blockMembers" +
            ") AS ranking " +
            "WHERE ranking.rn <= 1",nativeQuery = true)
    List<Long> findTopByHashTagWithoutBlockLists(@Param("hashTagId") List<Long> hashTagId, @Param("memberId") Long memberId, @Param("blockMembers") List<Long> members);
    @Query(value = "SELECT * " +
            "FROM ( " +
            "    SELECT ph.*, RANK() OVER (PARTITION BY ph.hash_tag ORDER BY ph.post_hash_id DESC) AS rn " +
            "    FROM post_hash AS ph " +
            "    JOIN post AS p ON ph.post_id = p.post_id " +
            "    WHERE ph.hash_tag IN :hashTagId AND p.is_locked = FALSE AND ((p.member_id = :memberId and p.for_me = true) or (p.for_me = false)) " +
            ") AS ranking " +
            "WHERE ranking.rn <= 1",nativeQuery = true)
    List<Long> findTopByHashTag(@Param("hashTagId") List<Long> hashTagId, @Param("memberId") Long memberId);
    @Query("select p.postId " +
            "from PostHash p " +
            "join fetch p.postId.memberId " +
            "where p.hashTagId = :hashTag and p.postId.isLocked = false and ((p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false)) " +
            "order by p.poshHashId desc")
    List<Post> findFirstPostHash(@Param("hashTag") HashTag hashTag, @Param("member") Member member, PageRequest pageRequest);
    @Query("select p.postId " +
            "from PostHash p " +
            "join fetch p.postId.memberId " +
            "where p.hashTagId = :hashTag and p.postId.memberId not in :members and p.postId.isLocked = false and (p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false) " +
            "order by p.poshHashId desc")
    List<Post> findFirstPostHashWithoutBlockLists(@Param("hashTag") HashTag hashTag, @Param("member") Member member, @Param("members") List<Member> members, PageRequest pageRequest);

    @Query("select p.postId " +
            "from PostHash p " +
            "join fetch p.postId.memberId " +
            "where p.hashTagId = :hashTag and p.poshHashId <= :pos and p.postId.isLocked = false and (p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false) " +
            "order by p.poshHashId desc ")
    List<Post> findPostHash(@Param("hashTag") HashTag hashTag , @Param("member") Member member, @Param("pos") Long pos, PageRequest pageRequest);
    @Query("select p.postId " +
            "from PostHash p " +
            "join fetch p.postId.memberId " +
            "where p.hashTagId = :hashTag and p.poshHashId <= :pos and p.postId.memberId not in :members and p.postId.isLocked = false and (p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false) " +
            "order by p.poshHashId desc ")
    List<Post> findPostHashWithoutBlockLists(@Param("hashTag") HashTag hashTag , @Param("member") Member member, @Param("pos") Long pos, @Param("members") List<Member> members, PageRequest pageRequest);
}
