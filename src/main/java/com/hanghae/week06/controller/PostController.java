package com.hanghae.week06.controller;


import com.hanghae.week06.controller.request.PostRequestDto;
import com.hanghae.week06.domain.UserDetailsImpl;
import com.hanghae.week06.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping("/api/post")
    public ResponseEntity<?> createPost(HttpServletRequest request,
                                        @ModelAttribute PostRequestDto requestDto
                                       ) throws IOException {
        return postService.createPost(requestDto, request );
    }

    // 게시글 조회
    @GetMapping("/api/post/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    // 게시글 목록 조회
    @GetMapping("/api/post")
    public ResponseEntity<?> getPostList(){
        return postService.getPostList();
    }

    // 게시글 수정
    @PutMapping("/api/post/{postId}")
    public ResponseEntity<?> editPost(@PathVariable Long postId,
                                   @RequestBody @Valid PostRequestDto requestDto,
                                   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return postService.editPost(postId, requestDto, userDetailsImpl);
    }

    // 게시글 삭제
    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        HttpServletRequest request ) {
        return postService.deletePost(postId, request );
    }

}
