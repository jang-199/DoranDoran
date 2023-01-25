package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.UserUploadPic;
import com.dorandoran.doranserver.repository.UserUploadPicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUploadPicServiceImpl implements UserUploadPicService{
    private final UserUploadPicRepository userUploadPicRepository;

    @Override
    public void saveUserUploadPic(UserUploadPic userUploadPic) {
        userUploadPicRepository.save(userUploadPic);
    }
}
