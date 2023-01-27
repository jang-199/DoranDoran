package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.entity.HashTag;
import com.dorandoran.doranserver.repository.HashTagRepository;
import com.dorandoran.doranserver.service.HashTagServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Slf4j
class APIControllerTest {

    private final HashTagServiceImpl hashTagService;
    private final HashTagRepository hashTagRepository;

    @Autowired
    APIControllerTest(HashTagServiceImpl hashTagService ,HashTagRepository hashTagRepository) {
        this.hashTagService = hashTagService;
        this.hashTagRepository = hashTagRepository;
    }

    @BeforeEach
    void saveHashTag(){
        HashTag build1 = HashTag.builder().hashTagName("1").hashTagCount(1L).build();
        hashTagService.saveHashTag(build1);
        HashTag build2 = HashTag.builder().hashTagName("2").hashTagCount(1L).build();
        hashTagService.saveHashTag(build2);
        HashTag build3 = HashTag.builder().hashTagName("3").hashTagCount(1L).build();
        hashTagService.saveHashTag(build3);
        List<HashTag> all = hashTagRepository.findAll();
        log.info("all : {}", all);
    }

    @Test
    void hashTagTest() {
        List<String> hashTagList = new ArrayList<>();
        hashTagList.add("1");
        hashTagList.add("2");
        hashTagList.add("4");


        HashTagDto hashTagDto = new HashTagDto(hashTagList);
        log.info("hashTagDto : {} ", hashTagList);

        if (!hashTagDto.getHashTagName().isEmpty()) {
            for (String hashTag : hashTagDto.getHashTagName()) {
                if (hashTagService.duplicateCheckHashTag(hashTag)) {
                    HashTag buildHashTag = HashTag.builder()
                            .hashTagName(hashTag)
                            .hashTagCount(1L)
                            .build();
                    hashTagService.saveHashTag(buildHashTag);
                } else {
                    Optional<HashTag> byHashTagName = hashTagService.findByHashTagName(hashTag);
                    if (byHashTagName.isPresent()) {
                        Long hashTagCount = byHashTagName.get().getHashTagCount();
                        byHashTagName.get().setHashTagCount(hashTagCount + 1);
                        hashTagService.saveHashTag(byHashTagName.get());
                    }
                }
            }
        }
        Optional<HashTag> byHashTagName = hashTagService.findByHashTagName("4");
        Assertions.assertThat(byHashTagName.get().getHashTagCount()).isEqualTo(1L);
    }
}