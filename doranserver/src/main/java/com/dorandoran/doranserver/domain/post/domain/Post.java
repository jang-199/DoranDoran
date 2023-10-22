package com.dorandoran.doranserver.domain.post.domain;

import com.dorandoran.doranserver.domain.common.entity.BaseEntity;
import com.dorandoran.doranserver.domain.member.domain.Member;
import com.dorandoran.doranserver.domain.background.domain.imgtype.ImgType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;
import org.springframework.lang.Nullable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "createdTime", column = @Column(name = "POST_TIME"))
@Builder
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long postId;

    @NotNull
    @Column(name = "CONTENT",length = 3000)
    private String content;

    @NotNull
    @Column(name = "FOR_ME")
    private Boolean forMe;

    @Nullable
    @Column(name = "LOCATION", columnDefinition = "GEOMETRY")
    private Point location;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "REPORT_COUNT")
    private int reportCount;

    @Column(name = "IS_LOCKED")
    private Boolean isLocked;

    @Column(name = "FONT")
    private String font;

    @Column(name = "FONT_COLOR")
    private String fontColor;

    @Column(name = "FONT_SIZE")
    private Integer fontSize;

    @Column(name = "FONT_BOLD")
    private Integer fontBold;

    public void addReportCount(){
        this.reportCount += 1;
    }

    public void setLocked(){this.isLocked = Boolean.TRUE;}
    public void setUnLocked(){this.isLocked = Boolean.FALSE;}
}
