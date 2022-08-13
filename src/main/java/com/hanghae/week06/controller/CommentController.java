package com.hanghae.week06.controller;

import com.hanghae.week06.controller.request.CommentRequestDto;
import com.hanghae.week06.domain.UserDetailsImpl;
import com.hanghae.week06.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/comment/{postId}") // 댓글 작성
    public ResponseEntity<String> createComment(@PathVariable Long postId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody CommentRequestDto commentRequestDto){
        return commentService.createComment(postId,userDetails.getMember().getId(),commentRequestDto);
    }

    @DeleteMapping("/api/comment/{commentId}") // 댓글 삭제
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId){
        return commentService.deleteComment(commentId);
    }

}
