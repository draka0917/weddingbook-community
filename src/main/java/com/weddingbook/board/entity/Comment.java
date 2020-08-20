package com.weddingbook.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private Long grp;

    @Column(nullable = false)
    private int seq;

    @Column(nullable = false)
    private int lvl;

    @Column(nullable = false, length = 200)
    private String contents;

    @Column(nullable = false, length = 1)
    private String delyn;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memno")
    private Member member;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment(Long grp, int seq, int lvl, String contents, String delyn, Member member, Board board) {
        this.grp = grp;
        this.seq = seq;
        this.lvl = lvl;
        this.contents = contents;
        this.member = member;
        this.board = board;
        this.delyn = delyn;
    }

    //Modified
    public Comment setUpdate(Long grp, int grp_seq, int lvl, String contents) {
        this.grp = grp;
        this.seq = grp_seq;
        this.lvl = lvl;
        this.contents = contents;

        return this;
    }

    //Delete
    public Comment setDelete(String delyn) {
        this.delyn = delyn;

        return this;
    }
}
