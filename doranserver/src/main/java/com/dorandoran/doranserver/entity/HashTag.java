package com.dorandoran.doranserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HASH_TAG_ID")
    private Long hashTagId;

    @NotNull
    @Column(name = "HASH_TAG_NAME")
    private String hashTagName;

    @NotNull
    @Column(name = "HASH_TAG_COUNT")
    private Long hashTagCount;
}
