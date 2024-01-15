package com.dorandoran.doranserver.domain.background.service;

import com.dorandoran.doranserver.domain.background.domain.UserUploadPic;


public interface UserUploadPicService {
    void saveUserUploadPic(UserUploadPic userUploadPic);

   UserUploadPic findUserUploadPicByName(String picName);
}
