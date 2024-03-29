package com.dorandoran.doranserver.domain.report.domain;

import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.comment.domain.Reply;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.report.domain.ReportType.ReportType;
import jakarta.persistence.*;
import lombok.*;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "REPLY_REPORT_TIME"))
public class ReportReply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_REPLY_ID")
    private Long ReportReplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_ID")
    private Reply replyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "REPORT_CONTENT")
    private String reportContent;

    @Builder
    public ReportReply(Reply replyId, Member memberId, ReportType reportType, String reportContent) {
        this.replyId = replyId;
        this.memberId = memberId;
        this.reportType = reportType;
        this.reportContent = reportContent;
    }
}
