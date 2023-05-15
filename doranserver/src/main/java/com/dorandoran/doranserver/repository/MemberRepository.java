package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    @Query("select m from Member m where m.refreshToken = :refreshToken")
    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);
}
