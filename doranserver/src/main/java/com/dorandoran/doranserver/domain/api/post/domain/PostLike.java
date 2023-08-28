package com.dorandoran.doranserver.domain.api.post.domain;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
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
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_LIKE_ID")
    private Long postLikeId;

    @Column(name = "CHECK_DELETE")
    private Boolean checkDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post postId;

    public void delete(){
        this.checkDelete = Boolean.TRUE;
    }

    public void restore(){
        this.checkDelete = Boolean.FALSE;
    }
}
