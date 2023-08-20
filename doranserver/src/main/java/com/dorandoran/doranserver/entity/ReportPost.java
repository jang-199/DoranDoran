package com.dorandoran.doranserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "POST_REPORT_TIME"))
public class ReportPost extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_POST_ID")
    private Long reportPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Column(name = "REPORT_CONTENT")
    private String reportContent;

    public ReportPost(Post postId, Member memberId, String reportContent) {
        this.postId = postId;
        this.memberId = memberId;
        this.reportContent = reportContent;
    }
}
