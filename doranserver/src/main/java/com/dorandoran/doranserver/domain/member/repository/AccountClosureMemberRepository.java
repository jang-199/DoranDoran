package com.dorandoran.doranserver.domain.member.repository;

import com.dorandoran.doranserver.domain.member.domain.AccountClosureMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountClosureMemberRepository extends JpaRepository<AccountClosureMember,Long> {
    @Query("select p from AccountClosureMember p " +
            "where p.closureMemberId.email = :email")
    Optional<AccountClosureMember> findAccountClosureMemberByEmail(@Param("email") String email);
}
