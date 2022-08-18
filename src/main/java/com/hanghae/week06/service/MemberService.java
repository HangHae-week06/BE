package com.hanghae.week06.service;

import com.hanghae.week06.controller.request.LoginRequestDto;
import com.hanghae.week06.controller.request.MemberRequestDto;
import com.hanghae.week06.controller.request.TokenDto;
import com.hanghae.week06.controller.response.MemberResponseDto;
import com.hanghae.week06.controller.response.ResponseDto;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.jwt.TokenProvider;
import com.hanghae.week06.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

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
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
//  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;


  @Transactional // 회원가입
  public ResponseEntity<?> createMember(MemberRequestDto requestDto) {

    // 회원가입을 시도하는 아이디가 중복되는지 검증하고, 중복이라면 에러를 출력한다.
    if (null != isPresentMember(requestDto.getLoginId())) {
      return new ResponseEntity<>( ResponseDto.fail("DUPLICATED_MEMBER_ID", "중복된 아이디 입니다." ) ,HttpStatus.CONFLICT );
    }

    // requestDto를 통해 새 회원을 생성한다.
    Member member = Member.builder()
            .memberId( requestDto.getLoginId())
            .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();

    memberRepository.save(member);  // 생성된 회원 DB에 저장

    return ResponseEntity.ok().body( ResponseDto.success(
        MemberResponseDto.builder()     // MemberResponseDto 양식으로 응답을 보낸다.
            .id(member.getId())
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()

    ) );
  }

  @Transactional  // 로그인
  public ResponseEntity<?> login(LoginRequestDto requestDto, HttpServletResponse response) {

    // 존재하는 회원 아이디인지 검증한다.
    Member member = isPresentMember( requestDto.getLoginId() );
    if (null == member) { // 존재하지 않는 회원일 경우 에러를 출력한다.
      return new ResponseEntity( ResponseDto.fail("MEMBER_NOT_FOUND","사용자를 찾을 수 없습니다.") , HttpStatus.NOT_FOUND);
    }

    //아이디의 비빌번호가 틀린지 검증한다.
    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {  // 비밀번호를 틀릴 경우 에러를 출력한다.
      return  new ResponseEntity( ResponseDto.fail("PASSWORD_FAIL","비밀번호를 틀렸습니다."), HttpStatus.NOT_FOUND );

    }

    // 로그인 성공 시 Access토큰과 Refresh토큰을 생성하여 응답 헤더에 실어준다.
    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseEntity.ok (ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .memberId((member.getMemberId()))
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build() )

    );
  }



  public ResponseEntity<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return new ResponseEntity( ResponseDto.fail("INVALID_TOKEN","Token이 유효하지 않습니다.") , HttpStatus.BAD_REQUEST );
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return new ResponseEntity( ResponseDto.fail("MEMBER_NOT_FOUND","사용자를 찾을 수 없습니다.") , HttpStatus.NOT_FOUND );

    }
    return tokenProvider.deleteRefreshToken(member);
  }

  // 존재하는 회원인지 검증한다.
  @Transactional(readOnly = true)
  public Member isPresentMember(String memberId) {
    Optional<Member> optionalMember = memberRepository.findByMemberId(memberId); // 회원 고유id로 회원을 찾는다.
    return optionalMember.orElse(null);
  }

  // 응답 헤더에 토큰값을 추가해준다.
  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
