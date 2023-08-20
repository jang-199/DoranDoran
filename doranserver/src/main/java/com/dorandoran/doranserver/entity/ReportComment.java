package com.dorandoran.doranserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "COMMENT_REPORT_TIME"))
public class ReportComment extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_COMMENT_ID")
    private Long ReportCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Column(name = "REPORT_CONTENT")
    private String reportContent;

    @Builder
    public ReportComment(Comment commentId, Member memberId, String reportContent) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.reportContent = reportContent;
    }
}
