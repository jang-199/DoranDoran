package com.dorandoran.doranserver.domain.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBlockList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOCK_LIST_ID")
    private Long BlockListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKING_MEMBER")
    private Member BlockingMember; //차단한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKED_MEMBER")
    private Member BlockedMember; //차단한 사용자에게 차단된 사용자

}
