package com.weddingbook.board.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CommentParam {
//    @NotNull
//    @ApiModelProperty(value = "댓글 ID", notes = "", required = true)
//    private Long commentId;

    @NotEmpty
    @Size(min = 2, max = 200)
    @ApiModelProperty(value = "내용", required = true)
    private String contents;

}
