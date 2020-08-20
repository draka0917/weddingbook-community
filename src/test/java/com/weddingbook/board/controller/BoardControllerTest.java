package com.weddingbook.board.controller;

import com.google.gson.Gson;
import com.weddingbook.board.advice.exception.SigninException;
import com.weddingbook.board.common.result.SingleResult;
import com.weddingbook.board.config.security.JwtTokenProvider;
import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.Member;
import com.weddingbook.board.repository.MemberRepository;
import com.weddingbook.board.service.BoardService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .addFilters(this.springSecurityFilterChain)
                .alwaysDo(print())
                .build();
    }

    //게시글 목록 정상 조회
    @Test
    public void test_board_list_search_normal() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("page", "1");

        //리스트조회
        mockMvc.perform(get("/apis/board/").params(params))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //게시글 작성
    @Test
    @WithMockUser
    public void test_board_write_normal() throws Exception {
        String token = signupCreateToken();

        MultiValueMap<String, String> boardParams = new LinkedMultiValueMap<>();

        boardParams.add("subject", "test입니다.");
        boardParams.add("contents", "내용입니다.");

        mockMvc.perform(post("/apis/board/")
            .params(boardParams)
            .header("X-AUTH-TOKEN",token))
            .andDo(print())
            .andExpect(status().isOk());

    }

    //글 작성 후 수정
    @Test
    @WithMockUser
    public void test_board_write_modify() throws Exception {
        String token = signupCreateToken();

        MultiValueMap<String, String> boardParams = new LinkedMultiValueMap<>();

        boardParams.add("subject", "test입니다.");
        boardParams.add("contents", "내용입니다.");

        //작성
        MvcResult result = mockMvc.perform(post("/apis/board/")
                .params(boardParams)
                .header("X-AUTH-TOKEN",token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Gson gson = new Gson();
        SingleResult<Board> singleResult = gson.fromJson(content, SingleResult.class);
        Map map = (Map) singleResult.getData();

        Double id = (Double) map.get("id");

        StringBuilder modifyUrl = new StringBuilder();
        modifyUrl.append("/apis/board/");
        modifyUrl.append((int) Math.abs(id));

//        //수정
        mockMvc.perform(put(modifyUrl.toString())
                .params(boardParams)
                .header("X-AUTH-TOKEN",token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //글 작성 후 삭제
    @Test
    @WithMockUser
    public void test_board_write_delete() throws Exception {
        String token = signupCreateToken();

        MultiValueMap<String, String> boardParams = new LinkedMultiValueMap<>();

        boardParams.add("subject", "test입니다.");
        boardParams.add("contents", "내용입니다.");

        //작성
        MvcResult result = mockMvc.perform(post("/apis/board/")
                .params(boardParams)
                .header("X-AUTH-TOKEN",token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Gson gson = new Gson();
        SingleResult<Board> singleResult = gson.fromJson(content, SingleResult.class);
        Map map = (Map) singleResult.getData();

        Double id = (Double) map.get("id");

        StringBuilder deleteUrl = new StringBuilder();
        deleteUrl.append("/apis/board/");
        deleteUrl.append((int) Math.abs(id));

//      삭제
        mockMvc.perform(delete(deleteUrl.toString())
                .params(boardParams)
                .header("X-AUTH-TOKEN",token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public String signupCreateToken() throws Exception {
        long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("id", "test01_"+epochTime+"@naver.com");
        params.add("password", "123456aa");

        //회원가입
        mockMvc.perform(post("/apis/signup").params(params))
                .andDo(print())
                .andExpect(status().isOk());

        //가입정보 조회
        Member member = memberRepository.findByUid("test01_"+epochTime+"@naver.com").orElseThrow(SigninException::new);

        //토큰생성
        String token = jwtTokenProvider.createToken(String.valueOf(member.getMemno()), member.getRoles());

        return token;
    }

}