package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @NotNull
    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @NotNull
    @Column(name = "NICKNAME")
    private String nickname;

    @NotNull
    @Column(name = "IDENTIFICATION_NUMBER")
    private String identificationNumber;

    @NotNull
    @Column(name = "SIGN_UP_DATE")
    private Date signUpDate;

    @NotNull
    @Column(name = "MEMBER_TOKEN")
    private String memberToken;

    @ManyToOne
    @JoinColumn(name = "POLICY_TERMS_ID")
    private PolicyTerms policyTermsId;
}
