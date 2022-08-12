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


  @NotBlank
  private String userid;

  @NotBlank
  private String nickname;

  @NotBlank
  private String password;

}
