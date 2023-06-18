package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface MemberHashRepository extends JpaRepository<MemberHash, Long> {
    @Query("SELECT mh from MemberHash mh where mh.member.email = :userEmail and mh.hashTagId.hashTagName = :hashTag")
    Optional<MemberHash> findMemberHashByEmailAndHashTag(@Param("userEmail") String userEmail, @Param("hashTag") String hashTag);
    List<MemberHash> findByMemberOrderByMemberHashId(Member member);
    @Query("select mh.hashTagId.hashTagName from MemberHash mh where mh.member.email = :userEmail")
    List<String> findByMember_Email(@Param("userEmail") String userEmail);
}
