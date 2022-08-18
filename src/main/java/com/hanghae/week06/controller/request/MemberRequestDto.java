package com.hanghae.week06.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  private String loginId; // 회원 아이디

  private String nickname;  // 회원 닉네임

  private String password;  // 회원 비밀번호

}
