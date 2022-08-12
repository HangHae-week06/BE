package com.hanghae.week06.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
  private boolean ok;
  private T result;

  public static <T> ResponseDto<T> success(T data) {
    return new ResponseDto<>(true, data );
  }

  public static <T> ResponseDto<T> fail(  T data  ) {
    return new ResponseDto<>(false, data );
  }


}
