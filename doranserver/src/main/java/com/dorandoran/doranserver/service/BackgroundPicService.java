package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.BackgroundPic;

import java.util.Optional;

public interface BackgroundPicService {
    public Optional<BackgroundPic> getBackgroundPic(String backgroundImgName);
}