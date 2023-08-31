package com.dorandoran.doranserver.domain.report.domain;

import com.dorandoran.doranserver.domain.comment.domain.Comment;
import com.dorandoran.doranserver.domain.member.domain.Member;
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
public class ReportComment {
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

    @Column(name = "REPORT_TIME")
    private LocalDateTime reportTime;
    @Builder
    public ReportComment(Comment commentId, Member memberId, String reportContent) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.reportContent = reportContent;
        this.reportTime = LocalDateTime.now();
    }
}
