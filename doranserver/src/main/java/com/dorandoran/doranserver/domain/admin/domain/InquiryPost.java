package com.dorandoran.doranserver.domain.admin.domain;

import com.dorandoran.doranserver.domain.api.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.admin.domain.inquirytype.InquiryStatus;
import com.dorandoran.doranserver.domain.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InquiryPost extends BaseEntity {
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
