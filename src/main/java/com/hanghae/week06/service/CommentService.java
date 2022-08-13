package com.hanghae.week06.service;

import com.hanghae.week06.controller.request.CommentRequestDto;
import com.hanghae.week06.controller.response.ResponseDto;
import com.hanghae.week06.domain.Comment;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.domain.Post;
import com.hanghae.week06.jwt.TokenProvider;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostService postService;
    private final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;


    @Transactional // 댓글 작성
    public ResponseEntity<?> createComment(Long postId, HttpServletRequest request, CommentRequestDto commentRequestDto) {
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>( ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
                    , HttpStatus.BAD_REQUEST );
        }

        Post post = postService.isPresentPost( postId );
        if (null == post) {
            return new ResponseEntity<>( ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.")
                    , HttpStatus.NOT_FOUND);
        }
        Comment comment = commentRepository.save(Comment.builder()
                .author(member.getNickname())
                .content(commentRequestDto.getContent())
                .member(member)
                .post(post)
                .build());
        return new ResponseEntity<>(ResponseDto.success( comment ), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId , HttpServletRequest request ){

        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(
                    ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
                    ,HttpStatus.BAD_REQUEST );
        }

        Comment comment = commentRepository.findById(commentId).orElse(null );

        if (null == comment) {
            return new ResponseEntity<>(ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.")
                    , HttpStatus.NOT_FOUND );
        }
        if (comment.validateMember(member)) {
            return new ResponseEntity<>(ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.")
                    , HttpStatus.BAD_REQUEST );
        }

        commentRepository.deleteById( commentId );
        return new ResponseEntity<>("삭제 완료!",HttpStatus.NO_CONTENT);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
