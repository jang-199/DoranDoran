package com.dorandoran.doranserver.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDto {
    Long commentId;
    String userEmail;
    String reply;
    Boolean anonymity;
    Boolean secretMode;
}

