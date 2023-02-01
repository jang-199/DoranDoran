package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

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
    @Column(name = "COMMENT_LIKE_TIME")
    private LocalDateTime commentLikeTime;

    @ManyToOne
    @JoinColumn(name = "COMMENT_ID")
    private Comment commentId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;
}
