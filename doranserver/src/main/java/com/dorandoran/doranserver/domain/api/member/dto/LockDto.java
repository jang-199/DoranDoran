package com.dorandoran.doranserver.domain.api.member.dto;

import com.dorandoran.doranserver.domain.api.member.domain.lockType.LockType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockDto {
    Duration lockTime;
    LockType lockType;
}
