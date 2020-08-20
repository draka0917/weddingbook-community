package com.weddingbook.board.service;

import com.weddingbook.board.advice.exception.NotOwnerException;
import com.weddingbook.board.advice.exception.ResourceNotExistException;
import com.weddingbook.board.advice.exception.UserNotFoundException;
import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.Member;
import com.weddingbook.board.entity.param.BoardParam;
import com.weddingbook.board.repository.BoardRepository;
import com.weddingbook.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //게시물 리스트 조회
    public Page<Board> findPostsByPaging(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
        pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        return boardRepository.findAll(pageable);
    }

    //게시글 ID 로 게시물 조회
    public Board getPost(long boardId) {
        return boardRepository.findById(boardId).orElseThrow(ResourceNotExistException::new);
    }

    //게시물 등록 (회원 ID 가 조회되지 않을경우 Exception)
    public Board writePost(String uid, BoardParam boardParam) {
        Board board = new Board(boardParam.getSubject(), boardParam.getContents(), memberRepository.findByUid(uid).orElseThrow(UserNotFoundException::new));
        return boardRepository.save(board);
    }

    //게시물 수정
    public Board updatePost(long boardId, String uid, BoardParam boardParam) {
        Board board = getPost(boardId);
        Member member = board.getMember();

        if (!uid.equals(member.getUid())) throw new NotOwnerException();

        board.setUpdate(boardParam.getSubject(), boardParam.getContents());

        return board;
    }

    //게시물 삭제
    public boolean deletePost(long boardId, String uid) {
        Board board = getPost(boardId);
        Member member = board.getMember();

        if (!uid.equals(member.getUid())) throw new NotOwnerException();

        boardRepository.delete(board);

        return true;
    }
}
