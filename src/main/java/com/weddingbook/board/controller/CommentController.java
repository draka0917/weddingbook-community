package com.weddingbook.board.controller;

import com.weddingbook.board.advice.exception.ResourceNotExistException;
import com.weddingbook.board.common.result.CommonResult;
import com.weddingbook.board.common.result.ListResult;
import com.weddingbook.board.common.result.SingleResult;
import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.Comment;
import com.weddingbook.board.entity.param.CommentParam;
import com.weddingbook.board.service.CommentService;
import com.weddingbook.board.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "3. Comment")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/apis/comment")
public class CommentController {

    private final ResponseService responseService;
    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록 조회", notes = "댓글 목록을 조회한다.")
    @GetMapping(value = "/{boardId}")
    public ListResult<Comment> comments(@PathVariable long boardId) {
        return responseService.getListResult(commentService.findComments(boardId));
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성한다.")
    @PostMapping(value = "/{boardId}/{commentId}")
    public SingleResult<Comment> comment(
            @ApiParam(value = "게시글 ID", required = true) @PathVariable long boardId,
            @ApiParam(value = "댓글 ID : 댓글일 경우 0, 대댓글의 경우 댓글 ID", required = true) @PathVariable long commentId,
            @Valid @ModelAttribute CommentParam commentParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        if (commentId == 0) {//댓글
            return responseService.getSingleResult(commentService.writeComment(uid, boardId, commentId, commentParam));
        } else { //대댓글
            return responseService.getSingleResult(commentService.writeCommentComment(uid, boardId, commentId, commentParam));
        }
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.")
    @DeleteMapping(value = "/{commentId}")
    public CommonResult deleteComment(@PathVariable long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        commentService.deleteComment("Y", commentId, uid);

        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "댓글 수정", notes = "댓글을 수정한다.")
    @PutMapping(value = "/{commentId}")
    public SingleResult<Comment> updateComment(@PathVariable long commentId, @Valid @ModelAttribute CommentParam commentParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        System.out.println("commentId = " + commentId);

        Comment comment = commentService.getComment(commentId);

        return responseService.getSingleResult(commentService.updateComment(comment.getGrp(), comment.getSeq(), comment.getLvl(),uid,commentId,commentParam));
    }

}
