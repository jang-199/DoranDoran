package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberHash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberHashRepository extends JpaRepository<MemberHash, Long> {
    List<MemberHash> findByMember(Member member);
}
