package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long postId;

    @NotNull
    @Column(name = "CONTENT")
    private String content;

    @NotNull
    @Column(name = "FOR_ME")
    private Boolean forMe;

    @Nullable
    @Column(name = "DELETE_DATE")
    private Date deleteDate;

    @Nullable
    @Column(name = "LOCATION")
    private String location;

    @NotNull
    @Column(name = "POST_TIME")
    private Date postTime;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @ManyToOne
    @JoinColumn(name = "BACKGROUND_PIC")
    private BackgroundPic backgroundPic;
}
