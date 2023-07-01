package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.PolicyTerms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyTermsRepository extends JpaRepository<PolicyTerms,Long> {
}
