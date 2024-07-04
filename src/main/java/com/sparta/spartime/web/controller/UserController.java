package com.sparta.spartime.web.controller;

import com.sparta.spartime.aop.envelope.Envelope;
import com.sparta.spartime.dto.request.UserEditProfileRequestDto;
import com.sparta.spartime.dto.request.UserSignupRequestDto;
import com.sparta.spartime.dto.request.UserWithdrawRequestDto;
import com.sparta.spartime.dto.response.UserResponseDto;
import com.sparta.spartime.entity.User;
import com.sparta.spartime.security.principal.UserPrincipal;
import com.sparta.spartime.service.AuthService;
import com.sparta.spartime.service.UserService;
import com.sparta.spartime.web.argumentResolver.annotation.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupRequestDto requestDto)  {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(requestDto));
    }

    @PostMapping("/{id}")
    @Envelope("회원탈퇴 처리가 완료되었습니다.")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody UserWithdrawRequestDto requestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.withdraw(id, requestDto, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Envelope("프로필 정보 변경에 성공했습니다.")
    public ResponseEntity<UserResponseDto> editProfile(@PathVariable Long id, @Valid @RequestBody UserEditProfileRequestDto requestDto, @LoginUser User loginUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.editProfile(id, requestDto, loginUser));
    }

    @GetMapping("/{id}")
    @Envelope("사용자 조회에 성공했습니다.")
    public ResponseEntity<UserResponseDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getProfile(id));
    }

    @GetMapping("/myprofile")
    public ResponseEntity<UserResponseDto> getProfile(@LoginUser User user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getMyProfile(user));
    }

    @DeleteMapping
    @Envelope("로그아웃 되었습니다.")
    public ResponseEntity<Void> logout(@LoginUser User user) {
        userService.logout(user);
        return ResponseEntity.noContent().build();
    }
}
