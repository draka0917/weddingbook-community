package com.weddingbook.board.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    //비밀번호 형식 오류
    @Test
    public void test_signup_password_not_valid() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("id", "test01@naver.com");
        params.add("password", "123456");

        //회원가입
        mockMvc.perform(post("/apis/signup").params(params))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(-2002));
    }

    //이메일 형식 오류
    @Test
    public void test_signup_email_not_valid() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("id", "testaver.com");
        params.add("password", "123456aa");

        //회원가입
        mockMvc.perform(post("/apis/signup").params(params))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(-2001));
    }

    //이메일 중복 가입 오류
    @Test
    public void test_signup_email_duplication_err() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("id", "test@naver.com");
        params.add("password", "123456aa");

        //회원가입
        mockMvc.perform(post("/apis/signup").params(params))
                .andDo(print())
                .andExpect(status().isOk());

        //회원가입
        mockMvc.perform(post("/apis/signup").params(params))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(-9999));
    }

    //틀린 계정 로그인 오류
    @Test
    public void test_signin_user_not_found() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("id", "nouser@naver.com");
        params.add("password", "123456aa");

        //로그인
        mockMvc.perform(post("/apis/signin").params(params))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(-1001));
    }

    //가입_로그인 정상
    @Test
    public void test_signup_signin_normal() throws Exception{
        long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("id", "test01_"+epochTime+"@naver.com");
        params.add("password", "123456aa");

        //회원가입
        mockMvc.perform(post("/apis/signup").params(params))
                .andDo(print())
                .andExpect(status().isOk());

        //로그인
        mockMvc.perform(post("/apis/signin").params(params))
                .andDo(print())
                .andExpect(status().isOk());
    }

}