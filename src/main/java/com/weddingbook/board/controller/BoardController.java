package com.weddingbook.board.controller;

import com.weddingbook.board.common.result.CommonResult;
import com.weddingbook.board.common.result.ListResult;
import com.weddingbook.board.common.result.SingleResult;
import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.param.BoardParam;
import com.weddingbook.board.service.BoardService;
import com.weddingbook.board.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "2. Board")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/apis/board")
public class BoardController {

    private final ResponseService responseService;
    private final BoardService boardService;

    @ApiOperation(value = "게시글 목록", notes = "게시글의 목록을 조회한다.")
    @GetMapping("/")
    public ListResult<Board> postsByPaging(
            @ApiIgnore @PageableDefault Pageable pageable,
            @ApiParam(value = "page", required = true) @RequestParam String page
            ) {

        Page<Board> boards = boardService.findPostsByPaging(pageable);

        return responseService.getListResult(boards);
    }

    @ApiOperation(value = "게시글 상세", notes = "게시글의 상세정보를 조회한다.")
    @GetMapping(value = "/{boardId}")
    public SingleResult<Board> post(@PathVariable long boardId) {
        return responseService.getSingleResult(boardService.getPost(boardId));
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성한다.")
    @PostMapping(value = "/")
    public SingleResult<Board> post(@Valid @ModelAttribute BoardParam boardParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        return responseService.getSingleResult(boardService.writePost(uid, boardParam));
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다.")
    @PutMapping(value = "/{boardId}")
    public SingleResult<Board> post(@PathVariable long boardId, @Valid @ModelAttribute BoardParam boardParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        return responseService.getSingleResult(boardService.updatePost(boardId, uid, boardParam));
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.")
    @DeleteMapping(value = "/{boardId}")
    public CommonResult deletePost(@PathVariable long boardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        boardService.deletePost(boardId, uid);
        return responseService.getSuccessResult();
    }
}
