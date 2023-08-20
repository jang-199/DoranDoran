package com.dorandoran.doranserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InquiryComment extends BaseEntity{
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
