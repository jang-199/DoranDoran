package com.dorandoran.doranserver.domain.background.service;

import com.dorandoran.doranserver.domain.background.domain.UserUploadPic;
import com.dorandoran.doranserver.domain.background.service.repository.UserUploadPicRepository;
import com.dorandoran.doranserver.domain.post.exception.CannotFoundUserUploadPicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUploadPicServiceImpl implements UserUploadPicService{
    private final UserUploadPicRepository userUploadPicRepository;

    @Override
    public void saveUserUploadPic(UserUploadPic userUploadPic) {
        userUploadPicRepository.save(userUploadPic);
    }

    @Override
    public UserUploadPic findUserUploadPicByName(String picName) {
        return userUploadPicRepository.findUserPicByName(picName)
                .orElseThrow(() -> new CannotFoundUserUploadPicException("유저 업로드 사진을 찾을 수 없습니다."));
    }
}
