package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.LockMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockMemberRepository extends JpaRepository<LockMember, Long> {
}
