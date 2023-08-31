package com.dorandoran.doranserver.domain.api.common.service;


import com.dorandoran.doranserver.domain.post.domain.Post;

import java.io.IOException;

public interface CommonService {
    Boolean compareEmails(String objectEmail, String userEmail);

    void deletePost(Post post) throws IOException;
}
