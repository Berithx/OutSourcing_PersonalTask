package com.sparta.spartime.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.spartime.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String intro;
    private int likedPostCount;
    private int likedCommentCount;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.intro = user.getIntro();
    }

    public UserResponseDto(User user, int likedPostCount, int likedCommentCount) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.intro = user.getIntro();
        this.likedPostCount = likedPostCount;
        this.likedCommentCount = likedCommentCount;
    }
}
