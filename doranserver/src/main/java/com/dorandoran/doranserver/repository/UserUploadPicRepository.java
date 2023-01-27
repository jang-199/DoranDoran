package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.UserUploadPic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserUploadPicRepository extends JpaRepository<UserUploadPic, Long> {
    Optional<UserUploadPic> findByImgName(String imgName);
}
