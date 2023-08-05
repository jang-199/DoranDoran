package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.AnonymityMember;
import com.dorandoran.doranserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnonymityMemberRepository extends JpaRepository<AnonymityMember, Long> {

    @Query("select a.userEmail from AnonymityMember a where a.postId = :postId")
    List<String> findAllUserEmail(@Param("postId") Post post);

    @Query("select a from AnonymityMember a where a.postId = :post")
    List<AnonymityMember> findAllByPost(@Param("post") Post post);

    @Modifying(clearAutomatically = true)
    @Query("delete from AnonymityMember a where a.postId = :post")
    void deleteByPostId(@Param("post") Post post);
}
