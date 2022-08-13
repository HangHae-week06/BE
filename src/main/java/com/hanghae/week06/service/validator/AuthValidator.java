package com.hanghae.week06.service.validator;

import com.hanghae.week06.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {

    public boolean isWriter(Member writer, Member member) {
        String writerId = writer.getMemberId();
        String userId = member.getMemberId();
        return writerId.equals(userId);
    }
}
