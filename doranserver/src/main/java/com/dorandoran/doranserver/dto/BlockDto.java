package com.dorandoran.doranserver.dto;

import com.dorandoran.doranserver.dto.blockMemberType.BlockType;
import com.dorandoran.doranserver.entity.Comment;
import com.dorandoran.doranserver.entity.Post;
import com.dorandoran.doranserver.entity.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BlockDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BlockMember{
        private BlockType blockType;
        private Long id;

        @Builder
        public BlockMember(BlockType blockType, Long id) {
            this.blockType = blockType;
            this.id = id;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BlockPostResponse {
        private Long postId;
        private String content;
        private String userEmail;

        @Builder

        public BlockPostResponse(Post post) {
            this.postId = post.getPostId();
            this.content = post.getContent();
            this.userEmail = post.getMemberId().getEmail();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BlockCommentResponse {
        private Long postId;
        private Long commentId;
        private String content;
        private String userEmail;

        @Builder
        public BlockCommentResponse(Comment comment) {
            this.postId = comment.getPostId().getPostId();
            this.commentId = comment.getCommentId();
            this.content = comment.getComment();
            this.userEmail = comment.getMemberId().getEmail();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BlockReplyResponse {
        private Long replyId;
        private String content;
        private String userEmail;

        @Builder
        public BlockReplyResponse(Reply reply){
            this.replyId = reply.getReplyId();
            this.content = reply.getReply();
            this.userEmail = reply.getMemberId().getEmail();
        }
    }

}
