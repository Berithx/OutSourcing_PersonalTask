package com.sparta.spartime.entity;

import com.sparta.spartime.dto.request.PostRequestDto;
import com.sparta.spartime.entity.common.TimeStamp;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Table(name = "posts")
@Builder
public class Post extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String contents;

    @Column(name = "likes", columnDefinition = "bigint default 0")
    private Long likes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        ANONYMOUS,
        NORMAL,
        NOTICE
    }

    public Post(PostRequestDto requestDto,Type type,User user) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContent();
        this.type = type;
        this.user = user;
        this.likes = 0L;
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContent();
    }

    public void updateLike(Long likes) {
        this.likes = likes;
    }
}
