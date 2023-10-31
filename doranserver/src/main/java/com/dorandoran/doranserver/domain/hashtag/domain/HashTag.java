package com.dorandoran.doranserver.domain.hashtag.domain;

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

    public void addHashtagCount(){
        this.hashTagCount += 1;
    }

    @Builder
    public HashTag(String hashTagName) {
        this.hashTagName = hashTagName;
        this.hashTagCount = 1L;
    }
}
