package com.dorandoran.doranserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDeleteDto {
    Long postId;
    String userEmail;
}
