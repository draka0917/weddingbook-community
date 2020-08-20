package com.weddingbook.board.controller;

import com.weddingbook.board.advice.exception.EmailFormatFailed;
import com.weddingbook.board.advice.exception.PasswordFormatFailed;
import com.weddingbook.board.advice.exception.SigninException;
import com.weddingbook.board.common.result.CommonResult;
import com.weddingbook.board.common.result.SingleResult;
import com.weddingbook.board.config.security.JwtTokenProvider;
import com.weddingbook.board.entity.Member;
import com.weddingbook.board.entity.param.MemberParam;
import com.weddingbook.board.repository.MemberRepository;
import com.weddingbook.board.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

//@Secured("ROLE_USER")
@Api(tags = {"1. Member"})
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/apis")
public class SignController {

    private final MemberRepository memberRepository;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "회원 가입", notes = "회원 가입을 합니다.")
    @PostMapping(value = "/signup")
    public CommonResult signup (
            @Valid @ModelAttribute MemberParam param, BindingResult bindingResult
            ) {

        //Format 체크
        for (FieldError error : bindingResult.getFieldErrors()) {
            if (error.getField().equals("id")) throw new EmailFormatFailed();
            if (error.getField().equals("password")) throw new PasswordFormatFailed();
        }
        
        memberRepository.save(
            Member.builder()
                .uid(param.getId())
                .password(passwordEncoder.encode(param.getPassword()))
                .roles(Collections.singletonList("ROLE_USER"))
                .build()
        );

        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "로그인", notes = "이메일, 비밀번호로 로그인 합니다.")
    @PostMapping(value = "/signin")
    public SingleResult<String> singin(
            @ApiParam(value = "ID : 이메일", required = true) @RequestParam String id,
            @ApiParam(value = "비밀번호", required = true) @RequestParam String password
            ) {
        Member member = memberRepository.findByUid(id).orElseThrow(SigninException::new);

        if (!passwordEncoder.matches(password, member.getPassword())) throw new SigninException();

        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(member.getMemno()), member.getRoles()));
    }

}
