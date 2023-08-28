package com.dorandoran.doranserver.domain.api.background.service;

import com.dorandoran.doranserver.domain.api.background.domain.UserUploadPic;


public interface UserUploadPicService {
    void saveUserUploadPic(UserUploadPic userUploadPic);

    public UserUploadPic findUserUploadPic(Long imgId) throws Exception;

    public UserUploadPic findUserUploadPicByName(String picName) throws Exception;
}
