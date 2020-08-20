package com.weddingbook.board.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
public class MemberParam {
    @NotEmpty
    @Email
    @ApiModelProperty(value = "ID : 이메일", required = true)
    private String id;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z]).{6,20}$")
    @ApiModelProperty(value = "비밀번호", required = true)
    private String password;

    public MemberParam(@NotEmpty @Email String id, @NotEmpty @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z]).{6,20}$") String password) {
        this.id = id;
        this.password = password;
    }
}
