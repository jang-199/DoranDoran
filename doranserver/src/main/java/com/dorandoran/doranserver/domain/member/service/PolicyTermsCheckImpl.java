package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.PolicyTerms;
import com.dorandoran.doranserver.domain.member.repository.PolicyTermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyTermsCheckImpl implements PolicyTermsCheck{

    private final PolicyTermsRepository policyTermsRepository;
    @Override
    public void policyTermsSave(PolicyTerms policyTerms) {
        policyTermsRepository.save(policyTerms);
    }
}
