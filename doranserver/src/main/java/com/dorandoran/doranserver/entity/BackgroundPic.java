package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
