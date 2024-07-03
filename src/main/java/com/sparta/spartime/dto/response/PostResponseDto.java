package com.sparta.spartime.dto.response;

import com.sparta.spartime.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final Long userId;
    private final Long likes;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContents();
        this.likes = post.getLikes();
        this.userId = post.getUser().getId();
        this.nickname = post.getType() == Post.Type.ANONYMOUS ? "ANONYMOUS" : post.getUser().getNickname();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public PostResponseDto(Post post, Post.Type type) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContents();
        this.likes = post.getLikes();
        this.userId = post.getUser().getId();
        this.nickname = "ANONYMOUS";
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public PostResponseDto(Long id, String title, String content, Long userId, Long likes, Post.Type type, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.likes = likes;
        this.nickname = type == Post.Type.ANONYMOUS ? "ANONYMOUS" : nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
