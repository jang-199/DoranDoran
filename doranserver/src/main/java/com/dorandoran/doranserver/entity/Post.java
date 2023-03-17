package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.imgtype.ImgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    @Column(name = "CONTENT",length = 1000)
    private String content;

    @NotNull
    @Column(name = "FOR_ME")
    private Boolean forMe;

    @Nullable
    @Column(name = "LATITUDE")
    private String latitude;

    @Nullable
    @Column(name = "LONGITUDE")
    private String longitude;

    @NotNull
    @Column(name = "POST_TIME")
    private LocalDateTime postTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Enumerated(EnumType.STRING)
    private ImgType switchPic;

    @NotNull
    @Column(name = "IMG_NAME")
    private String ImgName;

    @Column(name = "ANONYMITY")
    @ColumnDefault("True")
    private Boolean anonymity;

    @Column(name = "FONT")
    private String font;

    @Column(name = "FONT_COLOR")
    private String fontColor;

    @Column(name = "FONT_SIZE")
    private Integer fontSize;

    @Column(name = "FONT_BOLD")
    private Integer fontBold;
}
