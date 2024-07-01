package com.sparta.spartime.dto.response;

import com.sparta.spartime.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentResponseDto {
    private Long id;
    private String email;
    private String contents;
    private Long likes;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.email = comment.getUser().getEmail();
        this.contents = comment.getContents();
        this.likes = comment.getLikes();
        this.createAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
