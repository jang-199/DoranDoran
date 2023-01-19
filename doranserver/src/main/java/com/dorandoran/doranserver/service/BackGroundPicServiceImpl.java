package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.BackgroundPic;
import com.dorandoran.doranserver.repository.BackgroundPicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class BackGroundPicServiceImpl implements BackgroundPicService{
    @Value("${background.cnt}")
    Integer backgroundCnt;

    private final BackgroundPicRepository backgroundPicRepository;

    @Override
    public Optional<BackgroundPic> getBackgroundPic(Long backgroundId) {
//        return backgroundPicRepository.findByImgName(backgroundImgName);
        return backgroundPicRepository.findById(backgroundId);
    }

    @Override
    public void saveBackgroundPic(BackgroundPic backgroundPic) {
        backgroundPicRepository.save(backgroundPic);
    }


}
