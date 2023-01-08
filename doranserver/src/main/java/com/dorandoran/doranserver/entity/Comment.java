package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @NotNull
    @Column(name = "COMMENT")
    private String comment;

    @NotNull
    @Column(name = "COMMENT_LIKE_COUNT")
    @ColumnDefault("0")
    private Long commentLikeCount;

    @NotNull
    @Column(name = "COMMENT_TIME")
    private Date commentTime;

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post postId;
}
