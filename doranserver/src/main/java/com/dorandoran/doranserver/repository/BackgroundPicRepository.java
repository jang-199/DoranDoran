package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.BackgroundPic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackgroundPicRepository extends JpaRepository<BackgroundPic, Long> {
    Optional<BackgroundPic> findByImgName(String imgName);
}
