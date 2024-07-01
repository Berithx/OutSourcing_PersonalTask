package com.sparta.spartime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Basic
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD REQUEST"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"),

    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER NOT FOUND"),
    USER_INACTIVITY(HttpStatus.FORBIDDEN, "USER STATUS IS INACTIVITY"),
    USER_BLOCKED(HttpStatus.FORBIDDEN, "USER STATUS IS BLOCKED"),
    FAIL_AUTHENTICATION(HttpStatus.BAD_REQUEST, "USER EMAIL OR PASSWORD IS NOT CORRECT"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "INVALID REFRESH TOKEN"),
    NOT_MATCHED_USER(HttpStatus.BAD_REQUEST, "REQUEST USER, AUTH USER IS NOT MATCH"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID PASSWORD"),
    EXIST_USER(HttpStatus.BAD_REQUEST, "EXIST USER"),
    RECENT_USED_PASSWORD(HttpStatus.BAD_REQUEST, "RECENT USED PASSWORD"),

    // comment
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 게시글이 없거나 댓글이 없습니다."),
    COMMENT_NOT_USER(HttpStatus.FORBIDDEN, "수정 권한이 없습니다."),

    // like
    NO_SELF_LIKE(HttpStatus.BAD_REQUEST, "본인이 작성한 컨텐츠에는 좋아요를 할 수 없습니다."),
    LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "좋아요가 눌리지 않았습니다."),

    // follow
    ALREADY_FOLLOW(HttpStatus.BAD_REQUEST, "팔로우 중인 사용자입니다."),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, "팔로우 중인 사용자가 아닙니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
