package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolicyTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLICY_TERMS_ID")
    private Long policyTermsId;

    @NotNull
    @Column(name = "POLICY_1")
    private Boolean policy1;

    @NotNull
    @Column(name = "POLICY_2")
    private Boolean policy2;

    @NotNull
    @Column(name = "POLICY_3")
    private Boolean policy3;

    @NotNull
    @Column(name = "POLICY_4")
    private Boolean policy4;
}
