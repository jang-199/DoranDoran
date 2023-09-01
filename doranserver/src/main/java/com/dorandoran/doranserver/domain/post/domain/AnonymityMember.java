package com.dorandoran.doranserver.domain.post.domain;

import com.dorandoran.doranserver.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnonymityMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long anonymityMemberId;

    @Column(name = "USER_EMAIL")
    private String userEmail;

    @Column(name = "ANONYMITY_INDEX")
    private Long anonymityIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post postId;

    public AnonymityMember toEntity(String userEmail, Post post, Long nextIndex){
        return AnonymityMember.builder()
                .userEmail(userEmail)
                .postId(post)
                .anonymityIndex(nextIndex)
                .build();
    }
}
