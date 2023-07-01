package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.osType.OsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckRegisteredMemberDto {
    String email;
    OsType osType;
}
