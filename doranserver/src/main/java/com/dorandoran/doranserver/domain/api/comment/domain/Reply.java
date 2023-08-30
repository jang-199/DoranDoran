package com.dorandoran.doranserver.domain.api.comment.domain;

import com.dorandoran.doranserver.domain.api.member.domain.Member;
import com.dorandoran.doranserver.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "REPLY_TIME"))
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPLY_ID")
    private Long replyId;

    @NotNull
    @Column(name = "REPLY")
    private String reply;

    @Column(name = "ANONYMITY")
    @ColumnDefault("True")
    private Boolean anonymity;

    @Column(name = "CHECK_DELETE")
    private Boolean checkDelete;

    @Column(name = "SECRET_MODE")
    private Boolean secretMode;

    @Column(name = "REPORT_COUNT")
    private int reportCount;

    @Column(name = "IS_LOCKED")
    private Boolean isLocked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    public void setLocked(){this.isLocked = Boolean.TRUE;}
    public void setUnLocked(){this.isLocked = Boolean.FALSE;}
    public void addReportCount(){this.reportCount += 1;}

    public Boolean checkSecretMode(){
        return this.secretMode == Boolean.TRUE ? Boolean.TRUE : Boolean.FALSE;
    }
}
