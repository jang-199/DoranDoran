package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.UserUploadPic;

import javax.validation.constraints.NotNull;

public interface UserUploadPicService {
    void saveUserUploadPic(UserUploadPic userUploadPic);

    public UserUploadPic findUserUploadPic(Long imgId) throws Exception;
}
