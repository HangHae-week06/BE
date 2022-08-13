package com.hanghae.week06.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String content;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id",updatable = false)
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id",updatable = false)
    private Post post;

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
