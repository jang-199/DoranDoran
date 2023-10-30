package com.dorandoran.doranserver.global.util.fileextension;

import com.dorandoran.doranserver.domain.post.exception.UnsupportedImageExtensionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileExtensionsFilterTest {
    @Test
    @DisplayName("업로드 사진이 허용된 확장자")
    void isAvailableFileExtension() {
        //given
        FileExtensionsFilter fileExtensionsFilter = new FileExtensionsFilter();
        String availableFileExtension = "jpg";

        //when
        fileExtensionsFilter.isAvailableFileExtension(availableFileExtension);

        //then
        //아무 리턴, 예외가 생기지 않는다.
    }

    @Test
    @DisplayName("업로드 사진이 허용 되지 않은 확장자")
    void isNotAvailableFileExtension() {
        //given
        FileExtensionsFilter fileExtensionsFilter = new FileExtensionsFilter();
        String notAvailableFileExtension = "gif";

        //when, then
        Assertions.assertThatThrownBy(() -> fileExtensionsFilter.isAvailableFileExtension(notAvailableFileExtension))
                .isInstanceOf(UnsupportedImageExtensionException.class);
    }
}