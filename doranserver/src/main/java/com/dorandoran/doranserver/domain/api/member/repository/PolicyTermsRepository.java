package com.dorandoran.doranserver.domain.api.member.repository;

import com.dorandoran.doranserver.domain.api.member.domain.PolicyTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyTermsRepository extends JpaRepository<PolicyTerms,Long> {
}
