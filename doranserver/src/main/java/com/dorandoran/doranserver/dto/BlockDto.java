package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.dto.blockMemberType.BlockType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class BlockDto {

    @Getter
    @Setter
    public static class BlockMember{
        private BlockType blockType;
        private Long id;

        @Builder
        public BlockMember(BlockType blockType, Long id) {
            this.blockType = blockType;
            this.id = id;
        }
    }
}
