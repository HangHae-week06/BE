package com.hanghae.week06.repository;
import com.hanghae.week06.domain.Member;
import com.hanghae.week06.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<ShowPostList> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByMember(Member member);
    List<Post> findAllByCommentListIsNull();


    interface ShowPostList {
        Long getId();
        String getTitle();
        String getContent();
        String getimageUrl();
        String getAuthor();

        LocalDateTime getCreatedAt();
        LocalDateTime getModifiedAt();

    }
}