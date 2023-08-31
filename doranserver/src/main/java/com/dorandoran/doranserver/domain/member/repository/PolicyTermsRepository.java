package com.dorandoran.doranserver.domain.member.repository;

import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyTermsRepository extends JpaRepository<PolicyTerms,Long> {
}
