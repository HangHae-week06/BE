package com.hanghae.week06.repository;


import com.hanghae.week06.domain.Comment;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByMember(Member member);
  List<Post> findAllByCommentListIsNull();
}