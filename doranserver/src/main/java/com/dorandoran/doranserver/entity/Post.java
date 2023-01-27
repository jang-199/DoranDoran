package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.imgtype.ImgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    @Column(name = "LOCATION")
    private String location;

    @NotNull
    @Column(name = "POST_TIME")
    private LocalDateTime postTime;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @NotNull
    @Column(name = "SWITCH_PIC")
    private ImgType switchPic;

    @NotNull
    @Column(name = "IMG_NAME")
    private String ImgName;
}
