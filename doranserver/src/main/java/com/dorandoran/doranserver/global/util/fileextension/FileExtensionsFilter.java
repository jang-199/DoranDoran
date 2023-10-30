package com.dorandoran.doranserver.global.util.fileextension;

import com.dorandoran.doranserver.domain.post.exception.UnsupportedImageExtensionException;

import java.util.Arrays;

public class FileExtensionsFilter implements AllowedFileExtensions {
    public void isAvailableFileExtension(String fileExtension){
        String fileExtensionToLowerCase = fileExtension.toLowerCase();

        if (isNotExistedInAllowedFileExtension(fileExtensionToLowerCase)){
            throw new UnsupportedImageExtensionException("지원하지 않는 확장자입니다.");
        }
    }

    private boolean isNotExistedInAllowedFileExtension(String fileExtension){
        return Arrays.stream(allowedFileExtensions).noneMatch((allowedFileExtension) -> allowedFileExtension.equals(fileExtension));
    }
}
