package com.dorandoran.doranserver.global.util.fileextension;

import com.dorandoran.doranserver.domain.post.exception.UnsupportedImageExtensionException;

import java.util.Arrays;

public class FileExtensionFilter implements AllowedFileExtension{
    public void isAvailableFileExtension(String fileExtension){
        if ()){
            throw new UnsupportedImageExtensionException("지원하지 않는 확장자입니다.");
        }

    }

    private boolean isNotExistedInAllowedFileExtension(String fileExtension){
        return Arrays.stream(allowedFileExtension).noneMatch((fileExtensions) -> fileExtensions.equals(fileExtension);
    }
}
