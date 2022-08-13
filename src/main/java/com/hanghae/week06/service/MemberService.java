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

  @Transactional

  public ResponseEntity<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getLoginId())) {
      return new ResponseEntity( ResponseDto.fail("DUPLICATED_MEMBER_ID", "중복된 아이디 입니다." ) ,HttpStatus.CONFLICT );

    }

    Member member = Member.builder()
            .memberId( requestDto.getLoginId())
            .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();
    memberRepository.save(member);


    return ResponseEntity.ok().body( ResponseDto.success(

        MemberResponseDto.builder()
            .id(member.getId())
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()

    ) );
  }

  @Transactional
  public ResponseEntity<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember( requestDto.getLoginId() );
    if (null == member) {
      return new ResponseEntity( ResponseDto.fail("MEMBER_NOT_FOUND","사용자를 찾을 수 없습니다.") , HttpStatus.NOT_FOUND);
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return  new ResponseEntity( ResponseDto.fail("PASSWORD_FAIL","비밀번호를 틀렸습니다."), HttpStatus.NOT_FOUND );

    }

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

  @Transactional(readOnly = true)
  public Member isPresentMember(String memberId) {
    Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
