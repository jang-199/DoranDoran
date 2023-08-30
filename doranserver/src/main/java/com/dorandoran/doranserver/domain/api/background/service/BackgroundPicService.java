package com.dorandoran.doranserver.domain.api.background.service;

import com.dorandoran.doranserver.domain.api.background.domain.BackgroundPic;

import java.util.Optional;

public interface BackgroundPicService {
    public Optional<BackgroundPic> getBackgroundPic(Long backgroundId);

    public void saveBackgroundPic(BackgroundPic backgroundPic);
}
