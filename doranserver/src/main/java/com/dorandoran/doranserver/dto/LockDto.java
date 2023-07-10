package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.entity.lockType.LockType;
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
