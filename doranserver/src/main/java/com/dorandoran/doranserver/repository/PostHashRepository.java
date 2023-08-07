package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.PostHash;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostHashRepository extends JpaRepository<PostHash,Long> {
    List<PostHash> findPostHashByPostId(Post post);

    @Query("select p " +
            "from PostHash p " +
            "where p.hashTagId = :hashTag and p.postId.memberId not in :members " +
            "order by p.poshHashId desc " +
            "limit 1")
    Optional<PostHash> findTopByHashTagWithoutBlockLists(@Param("hashTag") HashTag hashTag, @Param("members") List<Member> members);
    @Query("select p " +
            "from PostHash p " +
            "where p.hashTagId = :hashTag " +
            "order by p.poshHashId desc " +
            "limit 1")
    Optional<PostHash> findTopByHashTag(@Param("hashTag") HashTag hashTag);

    @Query("select p " +
            "from PostHash p " +
            "where p.hashTagId = :hashTag " +
            "order by p.poshHashId desc")
    List<PostHash> findFirstPostHash(@Param("hashTag") HashTag hashTag, PageRequest pageRequest);
    @Query("select p " +
            "from PostHash p " +
            "where p.hashTagId = :hashTag and p.postId.memberId not in :members " +
            "order by p.poshHashId desc")
    List<PostHash> findFirstPostHashWithoutBlockLists(@Param("hashTag") HashTag hashTag, PageRequest pageRequest,@Param("members") List<Member> members);

    @Query("select p " +
            "from PostHash p " +
            "where p.hashTagId = :hashTag and p.poshHashId <= :pos " +
            "order by p.poshHashId desc ")
    List<PostHash> findPostHash(@Param("hashTag") HashTag hashTag, @Param("pos") Long pos, PageRequest pageRequest);
    @Query("select p " +
            "from PostHash p " +
            "where p.hashTagId = :hashTag and p.poshHashId <= :pos and p.postId.memberId not in :members " +
            "order by p.poshHashId desc ")
    List<PostHash> findPostHashWithoutBlockLists(@Param("hashTag") HashTag hashTag, @Param("pos") Long pos, PageRequest pageRequest,@Param("members") List<Member> members);
}
