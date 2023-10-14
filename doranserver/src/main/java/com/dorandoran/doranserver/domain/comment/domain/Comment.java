package com.dorandoran.doranserver.domain.comment.domain;

import com.dorandoran.doranserver.domain.comment.dto.CommentDto;
import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.post.domain.Post;
import com.dorandoran.doranserver.domain.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "COMMENT_TIME"))
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @NotNull
    @Column(name = "COMMENT")
    private String comment;

    @Column(name = "ANONYMITY")
    @ColumnDefault("True")
    private Boolean anonymity;

    @Column(name = "CHECK_DELETE")
    private Boolean checkDelete;

    @Column(name = "SECRET_MODE")
    private Boolean secretMode;

    @Column(name = "COUNT_REPLY")
    private int countReply;

    @Column(name = "REPORT_COUNT")
    private int reportCount;

    @Column(name = "IS_LOCKED")
    private Boolean isLocked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    public void setLocked(){this.isLocked = Boolean.TRUE;}
    public void setUnLocked(){this.isLocked = Boolean.FALSE;}
    public void addReportCount(){this.reportCount += 1;}
    public Boolean checkSecretMode(){
        return this.getSecretMode() == Boolean.TRUE ? Boolean.TRUE : Boolean.FALSE;
    }
    public Comment toEntity(CommentDto.CreateComment createCommentDto, Post post, Member member){
        return Comment.builder()
                .comment(createCommentDto.getComment())
                .postId(post)
                .memberId(member)
                .anonymity(createCommentDto.getAnonymity())
                .checkDelete(Boolean.FALSE)
                .secretMode(createCommentDto.getSecretMode())
                .isLocked(Boolean.FALSE)
                .build();
    }
    @Builder
    public Comment(String comment, Boolean anonymity, Boolean checkDelete, Boolean secretMode, int countReply, int reportCount, Boolean isLocked, Post postId, Member memberId, Long commentId) {
        this.commentId = commentId;
        this.comment = comment;
        this.anonymity = anonymity;
        this.checkDelete = checkDelete;
        this.secretMode = secretMode;
        this.countReply = countReply;
        this.reportCount = reportCount;
        this.isLocked = isLocked;
        this.postId = postId;
        this.memberId = memberId;
    }
}
