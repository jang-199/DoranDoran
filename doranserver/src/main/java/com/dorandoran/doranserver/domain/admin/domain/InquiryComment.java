package com.dorandoran.doranserver.domain.admin.domain;

import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InquiryComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INQUIRY_COMMENT_ID")
    private Long inquiryCommentId;

    @NotNull
    @Column(name = "COMMENT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "INQUIRY_HISTORY")
    private InquiryPost inquiryPostId;

    @Builder
    public InquiryComment(String comment, InquiryPost inquiryPostId) {
        this.comment = comment;
        this.inquiryPostId = inquiryPostId;
    }
}
