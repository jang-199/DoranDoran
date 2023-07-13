package com.dorandoran.doranserver.dto;

import lombok.Data;

@Data
public class RetrieveHashTagPostDto {
    public String hashtagName;
    public Long postCnt;
    public String location;

}
