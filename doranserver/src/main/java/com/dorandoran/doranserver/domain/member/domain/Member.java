package com.dorandoran.doranserver.domain.member.domain;

import com.dorandoran.doranserver.domain.notification.domain.osType.OsType;
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
    @Column(name = "EMAIL", columnDefinition = "VARBINARY(255) NOT NULL")
    private String email;

    @NotNull
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
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
    @Column(name = "REFRESH_TOKEN", length = 400)
    private String refreshToken;

    @Column(name = "CLOSURE_DATE")
    private LocalDate closureDate;

    @Column(name = "TOTAL_REPORT_COUNT")
    private int totalReportCount;

    @NotNull
    @Column(name = "CHECK_NOTIFICATION")
    private Boolean checkNotification;

    @Enumerated(EnumType.STRING)
    private OsType osType;

    public void setAccountClosureRequestTime() {
        this.closureDate = LocalDate.now();
    }

    public Boolean isEmptyClosureDate() {
        return closureDate==null ? Boolean.TRUE : Boolean.FALSE;
    }
    public void addTotalReportTime(){
        this.totalReportCount += 1;
    }
    public void subtractReportTime(){this.totalReportCount -= 1;}
    public Boolean checkNotification(){
        return this.checkNotification.equals(Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
    }

    public void updateNotificationStatus(){
        this.checkNotification = !this.checkNotification;
    }

    @Builder
    public Member(Long memberId, String email, LocalDate dateOfBirth, String nickname, LocalDateTime signUpDate, String firebaseToken, PolicyTerms policyTermsId, String refreshToken, LocalDate closureDate, OsType osType, Boolean checkNotification) {
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
        this.checkNotification = checkNotification;
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
