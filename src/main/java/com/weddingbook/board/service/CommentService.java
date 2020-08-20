package com.weddingbook.board.service;

import com.weddingbook.board.advice.exception.NotOwnerException;
import com.weddingbook.board.advice.exception.ResourceNotExistException;
import com.weddingbook.board.advice.exception.UserNotFoundException;
import com.weddingbook.board.entity.Comment;
import com.weddingbook.board.entity.Member;
import com.weddingbook.board.entity.param.CommentParam;
import com.weddingbook.board.repository.BoardRepository;
import com.weddingbook.board.repository.CommentRepository;
import com.weddingbook.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //댓글 ID 로 댓글 조회
    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(ResourceNotExistException::new);
    }

    //댓글 목록
    public List<Comment> findComments(Long boardId) {

        return commentRepository.findByBoardOrderByGrpDescSeqAsc(boardRepository.findById(boardId).orElseThrow(ResourceNotExistException::new));
    }

    //댓글 등록
    public Comment writeComment(String uid, Long boardId, Long commentId, CommentParam commentParam) {
        Comment comment = new Comment((long) 0, 1, 0, commentParam.getContents(), "N", memberRepository.findByUid(uid).orElseThrow(UserNotFoundException::new), boardRepository.findById(boardId).orElseThrow(ResourceNotExistException::new));
        Comment saveComment = commentRepository.save(comment);
        //commentParam.setCommentId(saveComment.getId());
        Comment upComment =  updateComment(saveComment.getId(), 1, 0, uid, saveComment.getId(), commentParam);

        return upComment;
    }

    //대댓글 등록
    public Comment writeCommentComment(String uid, Long boardId, Long commentId, CommentParam commentParam) {
        Comment comment = getComment(commentId);
        int result = commentRepository.updateSeqForSeq(comment.getGrp(), comment.getSeq());

        Comment insertComment = new Comment((long) comment.getGrp(), comment.getSeq()+1, comment.getLvl()+1, commentParam.getContents(), "N", memberRepository.findByUid(uid).orElseThrow(UserNotFoundException::new), boardRepository.findById(boardId).orElseThrow(ResourceNotExistException::new));

        return commentRepository.save(insertComment);
    }

    //Update Comment
    public Comment updateComment(Long grp, int seq, int lvl, String uid, Long commentId, CommentParam commentParam) {
        Comment comment = getComment(commentId);
        Member member = comment.getMember();

        if (!uid.equals(member.getUid())) throw new NotOwnerException();

        comment.setUpdate(grp, seq, lvl, commentParam.getContents());

        return comment;
    }

    public Comment deleteComment(String delyn, Long commentId, String uid) {
        Comment comment = getComment(commentId);
        Member member = comment.getMember();

        if (!uid.equals(member.getUid())) throw new NotOwnerException();

        comment.setDelete(delyn);

        return comment;
    }

}
