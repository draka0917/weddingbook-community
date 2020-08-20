package com.weddingbook.board.entity.param;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class BoardParam {
    @NotEmpty
    @Size(min = 2, max = 20)
    @ApiModelProperty(value = "제목", required = true)
    private String subject;

    @NotEmpty
    @Size(min = 2, max = 200)
    @ApiModelProperty(value = "내용", required = true)
    private String contents;

    public BoardParam(@NotEmpty @Size(min = 2, max = 20) String subject, @NotEmpty @Size(min = 2, max = 200) String contents) {
        this.subject = subject;
        this.contents = contents;
    }
}
