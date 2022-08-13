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

  @PostMapping("/api/member/signup")

  public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto requestDto) {

    return memberService.createMember(requestDto);
  }

  @PostMapping("/api/member/login")

  public ResponseEntity login(@RequestBody @Valid LoginRequestDto requestDto,
                              HttpServletResponse response

  ) {
    return memberService.login(requestDto, response);
  }

//  @RequestMapping(value = "/api/auth/member/reissue", method = RequestMethod.POST)
//  public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//    return memberService.reissue(request, response);
//  }

  @PostMapping("/api/auth/member/logout")

  public ResponseEntity<?> logout(HttpServletRequest request) {

    return memberService.logout(request);
  }
}
