package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
