package com.dorandoran.doranserver.domain.api.common.service;

import com.dorandoran.doranserver.domain.api.post.domain.Post;
import org.springframework.stereotype.Service;

import java.io.IOException;

public interface CommonService {
    Boolean compareEmails(String objectEmail, String userEmail);

    void deletePost(Post post) throws IOException;
}
