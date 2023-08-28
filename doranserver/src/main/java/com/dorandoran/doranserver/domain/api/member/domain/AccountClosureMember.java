package com.dorandoran.doranserver.domain.api.member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class AccountClosureMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLOSURE_ID")
    private Long closureId;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member closureMemberId;

    @Builder
    public AccountClosureMember(Long closureId, Member closureMemberId) {
        this.closureId = closureId;
        this.closureMemberId = closureMemberId;
    }
}
