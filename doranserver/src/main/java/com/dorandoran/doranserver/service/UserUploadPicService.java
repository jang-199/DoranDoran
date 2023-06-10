package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.UserUploadPic;


public interface UserUploadPicService {
    void saveUserUploadPic(UserUploadPic userUploadPic);

    public UserUploadPic findUserUploadPic(Long imgId) throws Exception;

    public UserUploadPic findUserUploadPicByName(String picName) throws Exception;
}
