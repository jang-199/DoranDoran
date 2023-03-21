package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.entity.BackgroundPic;
import com.dorandoran.doranserver.entity.UserUploadPic;
import com.dorandoran.doranserver.service.BackGroundPicServiceImpl;
import com.dorandoran.doranserver.service.UserUploadPicServiceImpl;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Controller
@Api(tags = "배경사진 관련 API")
public class BackGroundPicController {

    @Value("${userUpload.Store.path}")
    String userUploadPicServerPath;
    @Value("${background.Store.path}")
    String backgroundPicServerPath;

    @Value("${background.cnt}")
    Integer backgroundPicCnt;
    private final BackGroundPicServiceImpl backGroundPicService;
    private final UserUploadPicServiceImpl userUploadPicService;


    @ApiOperation(value = "배경화면 전체 개수", notes = "도란도란 서버가 지원하는 배경사진 총 개수를 반환하는 API입니다.")
    @ApiResponses(@ApiResponse(code = 200, message = "배경사진의 총 개수가 리턴됩니다."))
    @GetMapping("/background/maxcount")
    ResponseEntity<Integer> backgroundPic() {
        return ResponseEntity.ok().body(backgroundPicCnt);

    }

    @ApiOperation(value = "하나의 배경사진 조회", notes = "배경사진 번호를 통해 하나의 배경사진을 검색하여 반환하는 API입니다." +
            "\n\nCf.배경사진 번호는 1번 부터 시작됩니다." +
            "\n\nEx.배경사진이 100장이라면 1~100번까지의 사진을 조회할 수 있습니다.")
    @ApiResponses(
            @ApiResponse(code = 200, message = "배경사진이 이미지 파일로 반환됩니다.")
    )
    @GetMapping("/background/{picName}")
    ResponseEntity<Resource> eachBackground(@ApiParam(value = "배경사진 총 개수 이내의 사진 번호", required = true, example = "1") @PathVariable Long picName) throws MalformedURLException {

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
    }

    @GetMapping("/userpic/{picName}")
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
