package com.dorandoran.doranserver.domain.comment.domain;

import com.dorandoran.doranserver.domain.comment.dto.ReplyDto;
import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.member.domain.Member;
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

    @ManyToOne(fetch = FetchType.EAGER)
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
    public Reply toEntity(ReplyDto.CreateReply replyDto, Comment comment, Member member){
        return Reply.builder()
                .reply(replyDto.getReply())
                .anonymity(replyDto.getAnonymity())
                .commentId(comment)
                .memberId(member)
                .checkDelete(Boolean.FALSE)
                .secretMode(replyDto.getSecretMode())
                .isLocked(Boolean.FALSE)
                .build();
    }
}
