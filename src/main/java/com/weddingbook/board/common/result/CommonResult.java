package com.weddingbook.board.common.result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommonResult {

    @ApiModelProperty(value = "성공여부 : true / false")
    private boolean success;

    @ApiModelProperty(value = "응답코드 : code >= 0 normal , < 0 exception")
    private int code;

    @ApiModelProperty(value = "응답메시지")
    private String msg;
}
