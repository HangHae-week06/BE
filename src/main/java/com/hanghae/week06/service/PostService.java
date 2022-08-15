package com.hanghae.week06.service;

import com.hanghae.week06.controller.request.LoginRequestDto;
import com.hanghae.week06.controller.request.MemberRequestDto;
import com.hanghae.week06.controller.request.PostRequestDto;
import com.hanghae.week06.controller.request.TokenDto;
import com.hanghae.week06.controller.response.MemberResponseDto;
import com.hanghae.week06.controller.response.ResponseDto;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.domain.Post;
import com.hanghae.week06.domain.UserDetailsImpl;
import com.hanghae.week06.jwt.TokenProvider;
import com.hanghae.week06.repository.MemberRepository;
import com.hanghae.week06.repository.PostRepository;
import com.hanghae.week06.service.validator.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final AuthValidator authValidator;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseEntity<?> createPost(PostRequestDto requestDto,  HttpServletRequest request) {

//    if (userDetailsImpl == null) {
//      throw new IllegalArgumentException("로그인을 해주세요");
//    }
//
//    Post post = new Post(requestDto, userDetailsImpl);
//    postRepository.save(post);
//
//    return ResponseEntity.ok(ResponseDto.success("게시글이 작성되었습니다."));
    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>( ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
              , HttpStatus.BAD_REQUEST );
    }
    Post post = new Post(requestDto, member );
    postRepository.save( post );

    return ResponseEntity.ok(ResponseDto.success(post));
  }

  @Transactional
  public ResponseEntity<?> getPost(Long postId) {
    Post post = postRepository.findById(postId).orElse(null);
    if( post == null ){
      return new ResponseEntity<>(ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 입니다.")
              , HttpStatus.NOT_FOUND );
    }
    return ResponseEntity.ok(ResponseDto.success(post) );
  }

  @Transactional
  public ResponseEntity<?> getPostList() {
    return ResponseEntity.ok(ResponseDto.success(postRepository.findAllByOrderByCreatedAtDesc()));
  }

  @Transactional
  public ResponseEntity<?> editPost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetailsImpl) {
    Post post = postRepository.findById(postId).get();
    Member member = post.getMember();

    if (userDetailsImpl == null) {
      return ResponseEntity.ok(ResponseDto.fail("BAD_REQUEST","로그인이 필요합니다."));
    }

    if(authValidator.isWriter(member, userDetailsImpl.getMember())){
      post.update(requestDto);
      return ResponseEntity.ok(ResponseDto.success("게시글이 수정되었습니다."));

    } else throw new IllegalStateException("게시글을 수정할 권한이 없습니다.");

  }

  @Transactional
  public ResponseEntity<?> deletePost(Long postId, HttpServletRequest request ) {
//    Post post = postRepository.findById(postId).get();
//    Member member = userDetailsImpl.getMember();
//
//    if (userDetailsImpl == null) {
//      throw new IllegalArgumentException("로그인을 해주세요");
//    }
//
//    if(authValidator.isWriter(member, userDetailsImpl.getMember())){
//       postRepository.delete(post);
////      return ResponseEntity.ok(ResponseDto.success("성공적으로 삭제되었습니다."));
//
//    } else throw new IllegalStateException("게시글을 삭제할 권한이 없습니다.");

    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
              ,HttpStatus.BAD_REQUEST );
    }

    Post post = isPresentPost(postId);
    if (null == post) {
      return new ResponseEntity<>( ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.")
              , HttpStatus.NOT_FOUND );
    }
    if(authValidator.isWriter( post.getMember() , member )) {
      postRepository.delete(post);
      return ResponseEntity.ok(ResponseDto.success("성공적으로 삭제되었습니다."));
    } else {
      return new ResponseEntity<>(ResponseDto.fail("UNAUTHORIZED", "게시글을 삭제할 권한이 없습니다.")
              ,HttpStatus.UNAUTHORIZED );
    }
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
