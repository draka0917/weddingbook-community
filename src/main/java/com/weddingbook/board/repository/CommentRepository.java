package com.weddingbook.board.repository;

import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Query("update Comment u set u.seq = u.seq + 1 where u.grp = :grp and u.seq > :seq")
    int updateSeqForSeq(@Param("grp") Long grp, @Param("seq") int seq);

    List<Comment> findByBoardOrderByGrpDescSeqAsc(Board board);

}
