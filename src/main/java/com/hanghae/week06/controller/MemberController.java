package com.hanghae.week06.controller;

import com.hanghae.week06.controller.request.LoginRequestDto;
import com.hanghae.week06.controller.request.MemberRequestDto;

import com.hanghae.week06.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

//  회원가입
  @PostMapping("/api/member/signup")
  public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

//  로그인
  @PostMapping("/api/member/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto requestDto,
                              HttpServletResponse response
  ) {
    return memberService.login(requestDto, response);
  }

// 로그아웃
  @PostMapping("/api/member/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
