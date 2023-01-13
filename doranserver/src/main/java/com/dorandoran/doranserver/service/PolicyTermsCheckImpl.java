package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.repository.PolicyTermsRepository;
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
