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
import com.hanghae.week06.s3Upload.service.S3Uploader;
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
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final AuthValidator authValidator;
  private final TokenProvider tokenProvider;
  private final S3Uploader s3Uploader;

  @Transactional  // 게시글 생성
  public ResponseEntity<?> createPost(PostRequestDto requestDto,  HttpServletRequest request) throws IOException {

    // 토큰이 유효한지 확인한다.
    Member member = validateMember(request);
    if (null == member) {   // 유효한 토큰이 아닐 경우 에러를 출력한다.
      return new ResponseEntity<>( ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
              , HttpStatus.BAD_REQUEST );
    }

    // 이미지 파일을 s3에 저장하고, 이미지url을 result에 입력한다.
    String result = s3Uploader.uploadFiles( requestDto.getFile() , "static");

    // requestDto( 게시글 제목, 내용 )와 회원정보, 이미지url로 새 게시글을 생성한다.
    Post post = new Post( requestDto , member , result );

    postRepository.save( post );  // 게시글을 DB에 저장
    return ResponseEntity.ok(ResponseDto.success(post));
  }

  @Transactional  // 게시글 상세보기 페이지
  public ResponseEntity<?> getPost(Long postId) {

    // 게시글이 존재하는지 확인한다.
    Post post = postRepository.findById(postId).orElse(null);
    if( post == null ){ // 존재하지 않는 게시글일 경우 에러 출력
      return new ResponseEntity<>(ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 입니다.")
              , HttpStatus.NOT_FOUND );
    }
    return ResponseEntity.ok( ResponseDto.success( post ) );
  }

  @Transactional  // 전체 게시글 리스트 불러오기
  public ResponseEntity<?> getPostList() {
    return ResponseEntity.ok(ResponseDto.success(postRepository.findAllByOrderByCreatedAtDesc()));
  }

  @Transactional  // 게시글 수정
  public ResponseEntity<?> editPost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetailsImpl) {
    Post post = postRepository.findById(postId).get();
    Member member = post.getMember();

    if (userDetailsImpl == null) {
      return ResponseEntity.ok(ResponseDto.fail("BAD_REQUEST","로그인이 필요합니다."));
    }

    // 현재 회원과 게시글 작성자가 같은지 검증한다.
    if(authValidator.isWriter(member, userDetailsImpl.getMember())){  // 같을 경우 게시글 수정
      post.update(requestDto);
      return ResponseEntity.ok(ResponseDto.success("게시글이 수정되었습니다."));

    } else throw new IllegalStateException("게시글을 수정할 권한이 없습니다."); // 다를 경우 에러 출력

  }

  @Transactional  // 게시글 삭제
  public ResponseEntity<?> deletePost(Long postId, HttpServletRequest request ) {

    // 토큰이 유효한지 확인한다.
    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.")
              ,HttpStatus.BAD_REQUEST );
    }

    // 게시글이 존재하는지 확인한다.
    Post post = isPresentPost(postId);
    if (null == post) {
      return new ResponseEntity<>( ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.")
              , HttpStatus.NOT_FOUND );
    }

    // 현재 회원과 게시글 작성자가 같은지 검증한다.
    if(authValidator.isWriter( post.getMember() , member )) {
      // 같을 경우 삭제를 진행한다.
      String imageUrl = post.getImageUrl(); // 게시글의 이미지 Url
      String deleteUrl = imageUrl.substring(imageUrl.indexOf("static"));  //  이미지 url중 "/static" 이후의 주소만 따온다.
      s3Uploader.deleteImage(deleteUrl);  // s3에 저장된 이미지 삭제
      postRepository.delete(post);  // 게시글 삭제
      return ResponseEntity.ok(ResponseDto.success("성공적으로 삭제되었습니다."));
    } else {  // 다를 경우
      return new ResponseEntity<>(ResponseDto.fail("UNAUTHORIZED", "게시글을 삭제할 권한이 없습니다.")
              ,HttpStatus.UNAUTHORIZED );
    }
  }

  @Transactional(readOnly = true) // 존재하는 게시글인지 검증한다.
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {  // 토큰이 유효한지 검증한다.
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
