package com.dorandoran.doranserver.domain.post.service.common;


import com.dorandoran.doranserver.domain.post.domain.Post;

import java.io.IOException;

public interface PostCommonService {
    void deletePost(Post post) throws IOException;
}
