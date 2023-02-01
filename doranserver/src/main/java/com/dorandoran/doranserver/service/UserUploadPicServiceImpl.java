package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.UserUploadPic;
import com.dorandoran.doranserver.repository.UserUploadPicRepository;
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
    public UserUploadPic findUserUploadPic(Long imgId) throws Exception {
        Optional<UserUploadPic> byId = userUploadPicRepository.findById(imgId);
        if (byId.isPresent()) {
            return byId.get();
        }
        else {
            throw new Exception("유저 업로드 사진을 찾을 수 없습니다.");
        }
    }

    @Override
    public UserUploadPic findUserUploadPicByName(String picName) throws Exception {
        Optional<UserUploadPic> byImgName = userUploadPicRepository.findByImgNameLike(picName);
        if (byImgName.isPresent()) {
            return byImgName.get();
        }
        else {
            throw new Exception("유저 업로드 사진을 찾을 수 없습니다.");
        }
    }
}
