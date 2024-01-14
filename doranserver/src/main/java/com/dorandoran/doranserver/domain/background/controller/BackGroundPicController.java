package com.dorandoran.doranserver.domain.background.controller;

import com.dorandoran.doranserver.domain.background.domain.BackgroundPic;
import com.dorandoran.doranserver.domain.background.domain.UserUploadPic;
import com.dorandoran.doranserver.domain.background.service.BackgroundPicService;
import com.dorandoran.doranserver.domain.background.service.UserUploadPicService;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Timed
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Controller
public class BackGroundPicController {

    @Value("${cloud.aws.s3.bucket}")
    String bucket;

    @Value("${background.cnt}")
    Integer backgroundPicCnt;

    private final BackgroundPicService backGroundPicService;
    private final UserUploadPicService userUploadPicService;
    private final S3Client s3Client;


    @GetMapping("/pic/default/count")
    ResponseEntity<Integer> backgroundPic() {
        return ResponseEntity.ok().body(backgroundPicCnt);

    }

    @GetMapping("/pic/default/{picName}")
    public ResponseEntity<Resource> eachBackground(@PathVariable Long picName) {
        BackgroundPic backgroundPic = backGroundPicService.getBackgroundPic(picName).orElseThrow(()->new RuntimeException("해당 사진이 존재하지 않습니다."));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(backgroundPic.getServerPath()).build();
        ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] byteArray = object.asByteArray();
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backgroundPic.getImgName() + "\"")
                .body(byteArrayResource);

    }

    @GetMapping("/pic/member/{picName}")
    ResponseEntity<Resource> findUserUploadPic(@PathVariable String picName) {

        UserUploadPic userUploadPic = userUploadPicService.findUserUploadPicByName(picName);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(userUploadPic.getServerPath()).build();
        ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] byteArray = object.asByteArray();
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userUploadPic.getImgName() + "\"")
                .body(byteArrayResource);

    }
}