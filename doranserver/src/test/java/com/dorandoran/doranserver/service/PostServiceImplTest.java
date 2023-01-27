package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.PolicyTerms;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.imgtype.ImgType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class PostServiceImplTest {

    @Autowired
    PostServiceImpl postService;
    @Autowired
    MemberServiceImpl memberService;
    @Autowired
    PolicyTermsCheckImpl policyTermsCheck;

    @BeforeEach
    void setPost() {


    }
    @Test

    void findPost() {
        List<Post> post = postService.findFirstPost();
        for (Post post1 : post) {
            log.info("findFirstPost : {}",post1.getContent());
        }

        List<Post> post1 = postService.findPost(380L);
        for (Post post2 : post1) {
            log.info("findPost : {}",post2.getContent());
        }
    }
}