package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnonymityMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long anonymityMemberId;

    @Column(name = "USER_EMAIL")
    private String userEmail;

    @Column(name = "ANONYMITY_INDEX")
    private Long anonymityIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post postId;
}
