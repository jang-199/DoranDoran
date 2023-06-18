package com.dorandoran.doranserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundPic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BACKGROUND_PIC_ID")
    private Long backgroundPicId;

    @NotNull
    @Column(name = "SERVER_PATH")
    private String serverPath;

    @NotNull
    @Column(name = "IMG_NAME")
    private String imgName;
}
