package com.dorandoran.doranserver.repository;

import com.dorandoran.doranserver.entity.UserUploadPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserUploadPicRepository extends JpaRepository<UserUploadPic, Long> {
    Optional<UserUploadPic> findByImgNameLike(String imgName);
    @Query("select m from UserUploadPic m where m.imgName like :imgName%")
    Optional<UserUploadPic> findUserPicByName(@Param("imgName") String imgName);
}
