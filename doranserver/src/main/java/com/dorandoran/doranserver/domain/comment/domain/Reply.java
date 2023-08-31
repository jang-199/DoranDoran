package com.dorandoran.doranserver.domain.comment.domain;

import com.dorandoran.doranserver.domain.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPLY_ID")
    private Long replyId;

    @NotNull
    @Column(name = "REPLY")
    private String reply;

    @NotNull
    @Column(name = "REPLY_TIME")
    private LocalDateTime ReplyTime;

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
