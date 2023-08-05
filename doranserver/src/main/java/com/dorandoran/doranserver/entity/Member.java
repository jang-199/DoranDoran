package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.osType.OsType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @NotNull
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @NotNull
    @Column(name = "NICKNAME")
    private String nickname;

    @NotNull
    @Column(name = "SIGN_UP_DATE")
    private LocalDateTime signUpDate;

    @NotNull
    @Column(name = "FIREBASE_TOKEN")
    private String firebaseToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_TERMS_ID")
    private PolicyTerms policyTermsId;

    @NotNull
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @Column(name = "CLOSURE_DATE")
    private LocalDate closureDate;

    @Column(name = "TOTAL_REPORT_TIME")
    private int totalReportTime;

    public void setAccountClosureRequestTime() {
        this.closureDate = LocalDate.now();
    }

    public Boolean isEmptyClosureDate() {
        return closureDate==null ? Boolean.TRUE : Boolean.FALSE;
    }
    public void addTotalReportTime(){
        this.totalReportTime += 1;
    }
    @Enumerated(EnumType.STRING)
    private OsType osType;
    @Builder
    public Member(Long memberId, String email, LocalDate dateOfBirth, String nickname, LocalDateTime signUpDate, String firebaseToken, PolicyTerms policyTermsId, String refreshToken, LocalDate closureDate, OsType osType) {
        this.memberId = memberId;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.nickname = nickname;
        this.signUpDate = signUpDate;
        this.firebaseToken = firebaseToken;
        this.policyTermsId = policyTermsId;
        this.refreshToken = refreshToken;
        this.closureDate = closureDate;
        this.osType = osType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return "true";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
