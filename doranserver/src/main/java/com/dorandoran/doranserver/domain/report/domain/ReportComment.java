package com.dorandoran.doranserver.domain.report.domain;

import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.report.domain.ReportType.ReportType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "COMMENT_REPORT_TIME"))
public class ReportComment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_COMMENT_ID")
    private Long ReportCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "REPORT_CONTENT")
    private String reportContent;

    @Builder
    public ReportComment(Comment commentId, Member memberId, ReportType reportType, String reportContent) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.reportType = reportType;
        this.reportContent = reportContent;
    }
}
