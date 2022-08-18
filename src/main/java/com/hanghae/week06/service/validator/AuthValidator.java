package com.hanghae.week06.service.validator;

import com.hanghae.week06.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {

    // 작성자와 현재 로그인한 회원이 같은지 검증한다.
    public boolean isWriter(Member writer, Member member) {
        String writerId = writer.getMemberId(); // 작성자 회원 아이디
        String userId = member.getMemberId();   // 현재 회원 아이디
        return writerId.equals(userId);
    }
}
