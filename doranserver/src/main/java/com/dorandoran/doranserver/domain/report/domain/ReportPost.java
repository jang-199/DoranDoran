package com.dorandoran.doranserver.domain.report.domain;

import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.report.domain.ReportType.ReportType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "POST_REPORT_TIME"))
public class ReportPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_POST_ID")
    private Long reportPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "REPORT_CONTENT")
    private String reportContent;

    public ReportPost(Post postId, Member memberId, ReportType reportType, String reportContent) {
        this.postId = postId;
        this.memberId = memberId;
        this.reportType = reportType;
        this.reportContent = reportContent;
    }
}
