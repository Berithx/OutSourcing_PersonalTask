package com.sparta.spartime.service;

import com.sparta.spartime.dto.request.PostRequestDto;
import com.sparta.spartime.dto.response.PostResponseDto;
import com.sparta.spartime.entity.Like;
import com.sparta.spartime.entity.Post;
import com.sparta.spartime.entity.User;
import com.sparta.spartime.exception.BusinessException;
import com.sparta.spartime.exception.ErrorCode;
import com.sparta.spartime.repository.LikeRepository;
import com.sparta.spartime.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postrepository;
    private final LikeRepository likeRepository;


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
    public void like(Long postId,User user) {
        Post post = getPost(postId);

        // 좋아요
        if(likeRepository.existsByUser_IdAndReferenceTypeAndRefId(user.getId(), Like.ReferenceType.POST, postId)){
            throw new BusinessException(ErrorCode.LIKE_ALREADY_EXISTS);
        }
        Like like = new Like(user,post);
        post.Likes(post.getLikes()+1);
        likeRepository.save(like);
    }

    @Transactional
    public void unlike(Long postId,User user) {
        Post post = getPost(postId);

        // 좋아요
        Like like = likeRepository.findByUserIdAndReferenceTypeAndRefId(user.getId(), Like.ReferenceType.POST, postId).orElseThrow(
                () -> new BusinessException(ErrorCode.LIKE_NOT_FOUND)
        );
        post.Likes(post.getLikes()-1);
        likeRepository.delete(like);
    }




    //:::::::::::::::::::// 도구 //::::::::::::::::::://

    public Post getPost(Long postId) {
        return postrepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("없는 게시글 입니다.")
        );
    }

    private void userCheck(User user, Post post) {
        if(!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("게시판 주인이 아닙니다.");
        }
    }
}
