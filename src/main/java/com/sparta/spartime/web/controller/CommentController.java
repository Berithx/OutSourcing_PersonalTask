package com.sparta.spartime.web.controller;

import com.sparta.spartime.dto.request.CommentRequestDto;
import com.sparta.spartime.dto.response.CommentResponseDto;
import com.sparta.spartime.entity.User;
import com.sparta.spartime.security.principal.UserPrincipal;
import com.sparta.spartime.service.CommentService;
import com.sparta.spartime.web.argumentResolver.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@LoginUser User user,
                                                            @PathVariable("postId") Long postId,
                                                            @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(user, postId, requestDto));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@LoginUser User user,
                                                            @PathVariable("postId") Long postId,
                                                            @PathVariable("commentId") Long commentId,
                                                            @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.ok(commentService.updateComment(user, postId, commentId, requestDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@LoginUser User user,
                                           @PathVariable("postId") Long postId,
                                           @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(user, postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(@LoginUser User user,
                                         @PathVariable("postId") Long postId,
                                         @PathVariable("commentId") Long commentId) {
        commentService.likeComment(user, postId, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> unlikeComment(@LoginUser User user,
                                         @PathVariable("postId") Long postId,
                                         @PathVariable("commentId") Long commentId) {
        commentService.unlikeComment(user, postId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
