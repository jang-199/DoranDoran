package com.dorandoran.doranserver.entity;

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
    private Member BlockingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKED_MEMBER")
    private Member BlockedMember;

}
