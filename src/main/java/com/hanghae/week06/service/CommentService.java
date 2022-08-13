package com.hanghae.week06.service;

import com.hanghae.week06.controller.request.CommentRequestDto;
import com.hanghae.week06.domain.Comment;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.domain.Post;
import com.hanghae.week06.repository.CommentRepository;
import com.hanghae.week06.repository.MemberRepository;
import com.hanghae.week06.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;



    @Transactional // 댓글 작성
    public ResponseEntity<String> createComment(Long postId,Long memberId, CommentRequestDto commentRequestDto) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId).orElseThrow();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "등록 실패"));
        commentRepository.save(Comment.builder()
                .author(author)
                .content(commentRequestDto.getContent())
                .member(member)
                .post(post)
                .build());
        return new ResponseEntity<>("둥록 성공", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("삭제성공"));
        if (!comment.getAuthor().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            return new ResponseEntity<>("삭제 실패", HttpStatus.UNAUTHORIZED);
        }
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
