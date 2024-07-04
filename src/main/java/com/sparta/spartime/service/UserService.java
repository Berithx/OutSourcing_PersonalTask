package com.sparta.spartime.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spartime.dto.request.UserEditProfileRequestDto;
import com.sparta.spartime.dto.request.UserSignupRequestDto;
import com.sparta.spartime.dto.request.UserWithdrawRequestDto;
import com.sparta.spartime.dto.response.UserResponseDto;
import com.sparta.spartime.entity.Like;
import com.sparta.spartime.entity.QLike;
import com.sparta.spartime.entity.QPost;
import com.sparta.spartime.entity.User;
import com.sparta.spartime.exception.BusinessException;
import com.sparta.spartime.exception.ErrorCode;
import com.sparta.spartime.repository.UserRepository;
import com.sparta.spartime.security.principal.UserPrincipal;
import com.sparta.spartime.web.argumentResolver.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JPAQueryFactory jpaQueryFactory;

    public UserResponseDto signup(UserSignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String nickname = requestDto.getNickname();
        String intro = requestDto.getIntro();

        if (userRepository.existsByEmailOrNickname(email, nickname)) {
            throw new BusinessException(ErrorCode.EXIST_USER);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .intro(intro)
                .role(User.Role.USER)
                .status(User.Status.ACTIVITY)
                .build();

        // return
        return new UserResponseDto(userRepository.save(user));
    }

    @Transactional
    public void withdraw(Long id, UserWithdrawRequestDto requestDto, UserPrincipal userPrincipal) {
        User idUser = findById(id);
        User user = findByEmail(userPrincipal.getUsername());

        if (!idUser.getEmail().equals(user.getEmail())) {
            throw new BusinessException(ErrorCode.NOT_MATCHED_USER);
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (user.getStatus().equals(User.Status.INACTIVITY)) {
            throw new BusinessException(ErrorCode.USER_INACTIVITY);
        }

        user.withdraw();
    }

    @Transactional
    public UserResponseDto editProfile(Long id, UserEditProfileRequestDto requestDto, User loginUser) {
        if(!Objects.equals(id, loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_MATCHED_USER);
        }

        User user = findById(id);

        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (requestDto.getNewPassword() != null) {
            String recentPassword = user.getRecentPassword();
            List<String> recentPasswordList;
            if(recentPassword != null) {
                recentPasswordList = new ArrayList<>(Arrays.asList(recentPassword.split(",")));
            } else {
                recentPasswordList = new ArrayList<>();
            }

            if(recentPasswordList.stream().anyMatch(recent -> passwordEncoder.matches(requestDto.getNewPassword(), recent))) {
                throw new BusinessException(ErrorCode.RECENT_USED_PASSWORD);
            }
            for (String s : recentPasswordList) {
                System.out.println(s);
            }
            recentPasswordList = recentPasswordList.subList(Math.max(recentPasswordList.size() - 2, 0), recentPasswordList.size());
            recentPasswordList.add(user.getPassword());

            String updatePasswordList = String.join(",", recentPasswordList);

            user.updateRecentPassword(updatePasswordList);
        }

        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        String newNickname = requestDto.getNewNickname();
        String newIntro = requestDto.getNewIntro();

        user.editProfile(newPassword, newNickname, newIntro);

        return new UserResponseDto(user);
    }

    public UserResponseDto getProfile(Long id) {
        return new UserResponseDto(findById(id));
    }

    public UserResponseDto getMyProfile(User user) {
        User loginUser = findById(user.getId());

        QLike like = QLike.like;

        List<Like> likedPostCount = jpaQueryFactory.selectFrom(like)
                .where(like.user.id.eq(user.getId())
                        .and(like.referenceType.eq(Like.ReferenceType.POST)))
                .fetch();

        List<Like> likedCommentCount = jpaQueryFactory.selectFrom(like)
                .where(like.user.id.eq(user.getId())
                        .and(like.referenceType.eq(Like.ReferenceType.COMMENT)))
                .fetch();

        return new UserResponseDto(loginUser, likedPostCount.size(), likedCommentCount.size());
    }

    @Transactional
    public void logout(User loginUser) {
        User user = findById(loginUser.getId());
        user.deleteRefreshToken();
    }

    protected User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    protected User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    protected User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
