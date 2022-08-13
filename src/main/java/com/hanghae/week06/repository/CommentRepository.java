package com.hanghae.week06.repository;


import com.hanghae.week06.domain.Comment;
import com.hanghae.week06.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findAllByMember(Member member);
  Optional<Comment> findById(Long commentId );
}
