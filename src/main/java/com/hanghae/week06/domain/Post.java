package com.hanghae.week06.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae.week06.controller.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = true)
    private String imageUrl;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy="post")
    private Set<Comment> commentSet;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id",updatable = false)
    private Member member;


    public Post(PostRequestDto requestDto, UserDetailsImpl userDetailsImpl) {
        super();
        this.member = userDetailsImpl.getMember();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.imageUrl = requestDto.getImgUrl();
        this.author = member.getNickname();

    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.imageUrl = requestDto.getImgUrl();
    }
}
