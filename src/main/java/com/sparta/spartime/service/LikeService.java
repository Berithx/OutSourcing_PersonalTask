package com.sparta.spartime.service;

import com.sparta.spartime.entity.Like;
import com.sparta.spartime.entity.User;
import com.sparta.spartime.exception.BusinessException;
import com.sparta.spartime.exception.ErrorCode;
import com.sparta.spartime.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public void like(User user, Like.ReferenceType refType, Long refId) {
        existLike(user.getId(), refType, refId);
        likeRepository.save(
                Like.builder()
                        .refId(refId)
                        .user(user)
                        .referenceType(refType)
                        .build()
        );
    }

    public void unlike(User user, Like.ReferenceType refType, Long refId) {
        Like like = findLikeBy(user.getId(), refType, refId);
        likeRepository.delete(like);
    }

    protected Like findLikeBy(Long userId, Like.ReferenceType refType, Long refId) {
        return likeRepository.findByUserIdAndReferenceTypeAndRefId(userId, refType, refId).orElseThrow(
                () -> new BusinessException(ErrorCode.LIKE_NOT_FOUND)
        );
    }

    protected void existLike(Long userId, Like.ReferenceType refType, Long refId) {
        if (likeRepository.existsByUserIdAndReferenceTypeAndRefId(userId, refType, refId)) {
            throw new BusinessException(ErrorCode.LIKE_ALREADY_EXISTS);
        }
    }
 }
