package com.dorandoran.doranserver.domain.background.service.repository;

import com.dorandoran.doranserver.domain.background.domain.BackgroundPic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackgroundPicRepository extends JpaRepository<BackgroundPic, Long> {
    Optional<BackgroundPic> findByImgName(String imgName);
}
