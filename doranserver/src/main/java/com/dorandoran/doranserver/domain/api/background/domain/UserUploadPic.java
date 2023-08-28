package com.dorandoran.doranserver.domain.api.background.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUploadPic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_UPLOAD_PICTURE_ID")
    private Long userUploadPictureId;

    @NotNull
    @Column(name = "SERVER_PATH")
    private String serverPath;

    @NotNull
    @Column(name = "IMG_NAME")
    private String imgName;
}
