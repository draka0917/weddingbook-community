package com.weddingbook.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;
    @Column(nullable = false, length = 20)
    private String subject;
    @Column(nullable = false, length = 200)
    private String contents;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memno")
    private Member member;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public Board(String subject, String contents, Member member) {
        this.subject = subject;
        this.contents = contents;
        this.member = member;
    }

    //Modified
    public Board setUpdate(String subject, String contents) {
        this.subject = subject;
        this.contents = contents;

        return this;
    }
}
