package com.hanghae.week06.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
  private Long id;
  private String memberId;
  private String nickname;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
