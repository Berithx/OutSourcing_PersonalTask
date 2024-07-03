package com.sparta.spartime.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spartime.dto.request.CommentRequestDto;
import com.sparta.spartime.dto.response.CommentResponseDto;
import com.sparta.spartime.dto.response.PostResponseDto;
import com.sparta.spartime.entity.*;
import com.sparta.spartime.exception.BusinessException;
import com.sparta.spartime.exception.ErrorCode;
import com.sparta.spartime.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostService postService;
    private final LikeService likeService;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public CommentResponseDto createComment(User user, Long postId, CommentRequestDto requestDto) {
        Post post = postService.getPost(postId);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .contents(requestDto.getContents())
                .likes(0L)
                .build();

        return new CommentResponseDto(commentRepository.save(comment));
    }

    public List<CommentResponseDto> getComments(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );
        return comments.stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> myLikedComment(int page, int size, User user) {
        QLike like = QLike.like;
        QComment comment = QComment.comment;
        Pageable pageable = PageRequest.of(page, size);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(like.referenceType.eq(Like.ReferenceType.COMMENT));
        if (user != null) {
            builder.and(like.user.eq(user));
        } else {
            builder.and(like.user.isNull());
        }

        JPAQuery<CommentResponseDto> query = jpaQueryFactory.select(
                        Projections.constructor(CommentResponseDto.class,
                                comment.id,
                                comment.user.email,
                                comment.contents,
                                comment.likes,
                                comment.createdAt,
                                comment.updatedAt))
                .from(comment)
                .leftJoin(like).on(comment.id.eq(like.refId))
                .where(builder)
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<CommentResponseDto> response = query.fetch();

        return new PageImpl<>(response, pageable, query.stream().count());
    }

    @Transactional
    public CommentResponseDto updateComment(User user, Long postId, Long commentId, CommentRequestDto requestDto) {
        Comment comment = findComment(postId, commentId);
        checkUser(user.getId(), comment.getUser().getId());
        comment.updateComment(requestDto.getContents());
        return new CommentResponseDto(comment);
    }

    public void deleteComment(User user, Long postId, Long commentId) {
        Comment comment = findComment(postId, commentId);
        checkUser(user.getId(), comment.getUser().getId());
        commentRepository.delete(comment);
    }

    @Transactional
    public void likeComment(User user, Long postId, Long commentId) {
        Comment comment = findComment(postId, commentId);

        if (user.getId().equals(comment.getUser().getId())) {
            throw new BusinessException(ErrorCode.NO_SELF_LIKE);
        }

        likeService.like(user, Like.ReferenceType.COMMENT, commentId);
        comment.updateLikes(comment.getLikes()+1);
    }

    @Transactional
    public void unlikeComment(User user, Long postId, Long commentId) {
        Comment comment = findComment(postId, commentId);

        likeService.unlike(user, Like.ReferenceType.COMMENT, commentId);
        comment.updateLikes(comment.getLikes()-1);
    }

    public Comment findComment(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    private void checkUser(Long inputUserId, Long userId) {
        if (!Objects.equals(inputUserId, userId)) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_USER);
        }
    }
}
