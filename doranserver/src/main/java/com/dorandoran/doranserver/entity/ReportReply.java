package com.dorandoran.doranserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportReply {
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

    @Column(name = "REPORT_TIME")
    private LocalDateTime reportTime;

    @Builder
    public ReportReply(Reply replyId, Member memberId, String reportContent) {
        this.replyId = replyId;
        this.memberId = memberId;
        this.reportContent = reportContent;
        this.reportTime = LocalDateTime.now();
    }
}
