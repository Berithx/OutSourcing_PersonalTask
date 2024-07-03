package com.sparta.spartime.web.controller;


import com.sparta.spartime.dto.request.PostRequestDto;
import com.sparta.spartime.dto.response.PostResponseDto;
import com.sparta.spartime.entity.User;
import com.sparta.spartime.security.principal.UserPrincipal;
import com.sparta.spartime.service.PostService;
import com.sparta.spartime.web.argumentResolver.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostContrller {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> create(@Valid @RequestBody PostRequestDto requestDto,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(postService.create(requestDto,userPrincipal.getUser()));
    }

    @PostMapping("/anonymous")
    public ResponseEntity<PostResponseDto> createAnonymous(@Valid @RequestBody PostRequestDto requestDto,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(postService.createAnonymous(requestDto,userPrincipal.getUser()));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(postService.getPage(page-1,size,type));
    }

    @GetMapping("/mylikepost")
    public ResponseEntity<Page<PostResponseDto>> myLikePost(@RequestParam(defaultValue = "1") int page,
                                                                               @RequestParam(defaultValue = "5") int size,
                                                                               @LoginUser User user) {
        return ResponseEntity.ok(postService.myLikedPost(page-1, size, user));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> get(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.get(postId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> update(@Valid @RequestBody PostRequestDto requestDto, @PathVariable Long postId,
                                                  @LoginUser User user) {
        return ResponseEntity.ok(postService.update(requestDto, postId, user));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete( @PathVariable Long postId,
                                     @LoginUser User user) {
        postService.delete(postId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> like(@PathVariable Long postId,
                                  @LoginUser User user) {
        postService.likePost(postId, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlike(@PathVariable Long postId,
                                    @LoginUser User user) {
        postService.unlikePost(postId, user);
        return ResponseEntity.noContent().build();
    }
}
