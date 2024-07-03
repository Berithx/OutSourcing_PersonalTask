package com.sparta.spartime.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spartime.dto.request.PostRequestDto;
import com.sparta.spartime.dto.response.PostResponseDto;
import com.sparta.spartime.entity.*;
import com.sparta.spartime.exception.BusinessException;
import com.sparta.spartime.exception.ErrorCode;
import com.sparta.spartime.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postrepository;
    private final LikeService likeService;
    private final JPAQueryFactory jpaQueryFactory;


    public PostResponseDto create(PostRequestDto requestDto, User user ) {
        Post post = new Post(requestDto,Post.Type.NORMAL,user);
        postrepository.save(post);
        return new PostResponseDto(post);
    }

    public PostResponseDto createAnonymous(PostRequestDto requestDto ,User user) {
        Post post = new Post(requestDto, Post.Type.ANONYMOUS, user);
        postrepository.save(post);
        return new PostResponseDto(post,Post.Type.ANONYMOUS );
    }

    public Page<PostResponseDto> getPage(int page, int size, String type) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (type.equals("ANONYMOUS")) {
            Post.Type postType = Post.Type.valueOf(type.toUpperCase());
            return postrepository.findByType(postType, pageRequest).map(post -> new PostResponseDto(post, postType));
        } else {
            return postrepository.findAll(pageRequest).map(PostResponseDto::new);
        }
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> myLikedPost(int page, int size, User user) {
        QPost post = QPost.post;
        QLike like = QLike.like;
        Pageable pageable = PageRequest.of(page, size);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(like.referenceType.eq(Like.ReferenceType.POST));
        if (user != null) {
            builder.and(like.user.eq(user));
        } else {
            builder.and(like.user.isNull());
        }

        JPAQuery<PostResponseDto> query = jpaQueryFactory.select(
                Projections.constructor(PostResponseDto.class,
                        post.id,
                        post.title,
                        post.contents,
                        post.user.id,
                        post.likes,
                        post.type,
                        post.user.nickname,
                        post.createdAt,
                        post.updatedAt))
                .from(post)
                .leftJoin(like).on(post.id.eq(like.refId))
                .where(builder)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<PostResponseDto> response = query.fetch();

        return new PageImpl<>(response, pageable, query.stream().count());
    }

    public PostResponseDto get(Long postId) {
        Post post = getPost(postId);
        if (post.getType() == Post.Type.ANONYMOUS) {
            return new PostResponseDto(post,Post.Type.ANONYMOUS);
        }
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto update(PostRequestDto requestDto,Long postId,User user) {
        Post post = getPost(postId);
        userCheck(user, post);

        post.update(requestDto);
        return new PostResponseDto(post);
    }

    @Transactional
    public void delete(Long postId,User user) {
        Post post = getPost(postId);
        userCheck(user, post);
        postrepository.delete(post);
    }

    @Transactional
    public void likePost(Long postId,User user) {
        Post post = getPost(postId);

        // 좋아요
        if (user.getId().equals(post.getUser().getId())) {
            throw new BusinessException(ErrorCode.NO_SELF_LIKE);
        }

        likeService.like(user, Like.ReferenceType.POST, postId);
        post.updateLike(post.getLikes()+1);
    }

    @Transactional
    public void unlikePost(Long postId,User user) {
        Post post = getPost(postId);

        // 좋아요
        likeService.unlike(user, Like.ReferenceType.POST, postId);
        post.updateLike(post.getLikes()-1);
    }

    public Post getPost(Long postId) {
        return postrepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("없는 게시글 입니다.")
        );
    }

    private void userCheck(User user, Post post) {
        if(!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시글 주인이 아닙니다.");
        }
    }
}
