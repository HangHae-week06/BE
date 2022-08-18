package com.hanghae.week06.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

  private MultipartFile file; // 이미지 파일
  private String title; // 게시글 제목
  private String content;   // 게시글 본문

}
