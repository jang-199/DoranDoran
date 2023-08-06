package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.inquirystatus.InquiryStatus;
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
@EntityListeners(AuditingEntityListener.class)
public class InquiryPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INQUIRY_POST_ID")
    private Long inquiryPostId;

    @NotNull
    @Column(name = "TITLE")
    private String title;

    @NotNull
    @Column(name = "CONTENT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InquiryStatus inquiryStatus;

    @CreatedDate
    private LocalDateTime createdTime;

    @Builder
    public InquiryPost(String title, String content, Member memberId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.inquiryStatus = InquiryStatus.NotAnswered;
    }

    public void updateStatus() {
        this.inquiryStatus = InquiryStatus.Answered;
    }
}
