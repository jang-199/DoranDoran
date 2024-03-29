package com.dorandoran.doranserver.domain.background.service;

import com.dorandoran.doranserver.domain.background.domain.BackgroundPic;
import com.dorandoran.doranserver.domain.background.service.repository.BackgroundPicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
