package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.BackgroundPic;
import com.dorandoran.doranserver.repository.BackgroundPicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BackGroundPicServiceImpl implements BackgroundPicService{
    private final BackgroundPicRepository backgroundPicRepository;

    @Override
    public Optional<BackgroundPic> getBackgroundPic(String backgroundImgName) {
        return backgroundPicRepository.findByImgName(backgroundImgName);
    }
}
