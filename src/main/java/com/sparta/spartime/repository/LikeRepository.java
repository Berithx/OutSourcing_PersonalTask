
package com.sparta.spartime.repository;

import com.sparta.spartime.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndReferenceTypeAndRefId(Long userId, Like.ReferenceType referenceType, Long refId);
    Optional<Like> findByUserIdAndReferenceTypeAndRefId(Long userId, Like.ReferenceType referenceType, Long referenceId);

}
