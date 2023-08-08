package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.Jackson2JsonRedisDto;
import com.dorandoran.doranserver.entity.BackgroundPic;
import com.dorandoran.doranserver.entity.UserUploadPic;
import com.dorandoran.doranserver.service.BackgroundPicService;
import com.dorandoran.doranserver.service.UserUploadPicService;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.Optional;

@Timed
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Controller
public class BackGroundPicController {

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;

    @Value("${background.cnt}")
    Integer backgroundPicCnt;
    private final BackgroundPicService backGroundPicService;
    private final UserUploadPicService userUploadPicService;
//    private final RedisTemplate<String, Jackson2JsonRedisDto> redisTemplate;


    @GetMapping("/pic/default/count")
    ResponseEntity<Integer> backgroundPic() {
        return ResponseEntity.ok().body(backgroundPicCnt);

    }

//    @Cacheable(value = "UrlResource")
    @GetMapping("/pic/default/{picName}")
    public ResponseEntity<Resource> eachBackground(@PathVariable Long picName) throws MalformedURLException {
        long start1 = System.currentTimeMillis();
        Optional<BackgroundPic> backgroundPic = backGroundPicService.getBackgroundPic(picName);


        if (backgroundPic.isPresent()) {
            UrlResource urlResource = new UrlResource("file:" + backgroundPic.get().getServerPath());
            log.info("{}", backgroundPic.get().getBackgroundPicId());
            log.info("{}", backgroundPic.get().getImgName());
            log.info("{}", backgroundPic.get().getServerPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backgroundPic.get().getImgName() + "\"")
                    .body(urlResource);
        } else {
            throw new RuntimeException("해당 사진이 존재하지 않습니다.");
        }
//
//        Jackson2JsonRedisDto jackson2JsonREdisDto = redisTemplate.opsForValue().get(picName.toString());
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + jackson2JsonREdisDto.getFileName() + "\"")
//                    .body(new ByteArrayResource(jackson2JsonREdisDto.getPic()));
    }

    @GetMapping("/pic/member/{picName}")
    ResponseEntity<Resource> findUserUploadPic(@PathVariable String picName) {

        try {
            UserUploadPic userUploadPic = userUploadPicService.findUserUploadPicByName(picName);
            UrlResource urlResource = new UrlResource("file:" + userUploadPic.getServerPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userUploadPic.getImgName() + "\"")
                    .body(urlResource);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
