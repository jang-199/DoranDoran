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

    @Query("select ph from PostHash ph join fetch ph.hashTagId where ph.postId = :post")
    List<PostHash> findPostHashByPostId(@Param("post") Post post);

    @Query("select p.postId " +
            "from PostHash p " +
            "where p.postId.postId in (" +
            "    select max(ph.postId.postId) " +
            "    from PostHash ph" +
            "    where ph.postId in (" +
            "       select ph.postId" +
            "       from PostHash ph" +
            "       where ph.hashTagId in :hashTag and p.postId.isLocked = false and ((p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false)) and ph.postId.memberId not in :blockMembers" +
            "   )" +
            "    group by ph.hashTagId" +
            ")")
    List<Post> findTopByHashTagWithoutBlockLists(@Param("hashTag") List<HashTag> hashTag, @Param("member") Member member, @Param("blockMembers") List<Member> members);
    @Query("select p.postId " +
            "from PostHash p " +
            "where p.postId.postId in (" +
            "    select max(ph.postId.postId) " +
            "    from PostHash ph" +
            "    where ph.postId in (" +
            "       select ph.postId" +
            "       from PostHash ph" +
            "       where ph.hashTagId in :hashTag and p.postId.isLocked = false and ((p.postId.memberId = :member and p.postId.forMe = true) or (p.postId.forMe = false))" +
            "   )" +
            "    group by ph.hashTagId" +
            ")")
    List<Post> findTopByHashTag(@Param("hashTag") List<HashTag> hashTag, @Param("member") Member member);
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
