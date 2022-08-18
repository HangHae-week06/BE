package com.hanghae.week06.service;

import com.hanghae.week06.controller.request.CommentRequestDto;
import com.hanghae.week06.controller.response.ResponseDto;
import com.hanghae.week06.domain.Comment;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.domain.Post;
import com.hanghae.week06.jwt.TokenProvider;
import com.hanghae.week06.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostService postService;
    private final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;


    @Transactional // 댓글 작성
    public ResponseEntity<?> createComment(Long postId, HttpServletRequest request, CommentRequestDto commentRequestDto) {

        // 현재 회원의 토큰이 유효한지 검증한다.
        Member member = validateMember(request);
        if (null == member) {   // 토큰이 유효하지 않을 경우 에러 출력한다.
            return new ResponseEntity<>( ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
                    , HttpStatus.BAD_REQUEST );
        }

        // 게시글이 존재하는지 확인한다.
        Post post = postService.isPresentPost( postId );
        if (null == post) {     // 게시글이 존재하지 않을 경우 에러를 출력한다.
            return new ResponseEntity<>( ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.")
                    , HttpStatus.NOT_FOUND);
        }

        // commentrequestDto를 통해 새 댓글을 생성하고, DB에 저장한다.
        Comment comment = commentRepository.save(Comment.builder()
                .author(member.getNickname())
                .content(commentRequestDto.getContent())
                .member(member)
                .post(post)
                .build());
        return new ResponseEntity<>(ResponseDto.success( comment ), HttpStatus.CREATED );
    }

    @Transactional  // 댓글 삭제
    public ResponseEntity<?> deleteComment(Long commentId , HttpServletRequest request ){

        // 현재 회원의 토큰이 유효한지 검증한다.
        Member member = validateMember(request);
        if (null == member) { // 토큰이 유효하지 않을 경우 에러 출력한다.
            return new ResponseEntity<>(
                    ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
                    ,HttpStatus.BAD_REQUEST );
        }

        // 댓글 고유id로 댓글을 찾는다.
        Comment comment = commentRepository.findById(commentId).orElse(null );

        if (null == comment) {  // 댓글을 찾지 못할 경우 에러 출력
            return new ResponseEntity<>(ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.")
                    , HttpStatus.NOT_FOUND );
        }
        if (comment.validateMember(member)) {   // 현재 회원과 댓글을 작성한 회원이 같은지 검증한 후 , 아닐 경우 에러 출력
            return new ResponseEntity<>(ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.")
                    , HttpStatus.BAD_REQUEST );
        }

        commentRepository.deleteById( commentId );  // 댓글 DB에서 삭제
        return ResponseEntity.ok( ResponseDto.success("삭제 완료!") );
    }

    // 현재 회원의 토큰이 유효한지 검증한다.
    @Transactional
    public Member validateMember(HttpServletRequest request)  {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
