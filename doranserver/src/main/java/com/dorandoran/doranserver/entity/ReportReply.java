package com.dorandoran.doranserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "REPLY_REPORT_TIME"))
public class ReportReply extends BaseEntity{
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

    @Column(name = "REPORT_CONTENT")
    private String reportContent;

    @Builder
    public ReportReply(Reply replyId, Member memberId, String reportContent) {
        this.replyId = replyId;
        this.memberId = memberId;
        this.reportContent = reportContent;
    }
}
