package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByNickname(String nickname);
}
