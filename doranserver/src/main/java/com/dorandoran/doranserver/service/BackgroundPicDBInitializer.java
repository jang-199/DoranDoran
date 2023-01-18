package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.BackgroundPic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BackgroundPicDBInitializer {

    @Autowired BackGroundPicServiceImpl backGroundPicService;
    @Value("${background.cnt}")
    Integer max;

    @Value("${background.Store.path}")
    String serverPath;

    @PostConstruct
    public void init() {
        for (int i = 0; i < max; i++) {
            BackgroundPic build = BackgroundPic.builder().imgName(i+1 + ".jpg")
                    .serverPath(serverPath + (i+1) + ".jpg")
                    .build();
            backGroundPicService.saveBackgroundPic(build);
        }
    }
}
