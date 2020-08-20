package com.weddingbook.board.controller;

import com.google.gson.Gson;
import com.weddingbook.board.advice.exception.SigninException;
import com.weddingbook.board.common.result.SingleResult;
import com.weddingbook.board.config.security.JwtTokenProvider;
import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.Comment;
import com.weddingbook.board.entity.Member;
import com.weddingbook.board.repository.MemberRepository;
import com.weddingbook.board.service.CommentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentService commentService;

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
    //댓글 입력/조회 테스트
    @Test
    public void test_comments() throws Exception {
        //게시글 입력
        String token = signupCreateToken();
        //작성
        String content = boardWrite(token);
        Double boardId = extractId(content);

        //댓글 작성
        String commentResult = commentWrite(token, boardId, 0.0);

        //댓글확인
        Double commentId = extractId(commentResult);

        Comment comment = commentService.getComment((int) Math.abs(commentId));

        assertNotNull(comment);

        //댓글목록
        StringBuilder url = new StringBuilder();
        url.append("/apis/comment/");
        url.append((int) Math.abs(boardId));

        mockMvc.perform(get(url.toString()))
                .andDo(print())
                .andExpect(status().isOk());

    }

    //댓글입력 테스트
    @Test
    public void test_comment() throws Exception {
        //게시글 입력
        String token = signupCreateToken();
        //작성
        String content = boardWrite(token);
        Double boardId = extractId(content);

        //댓글 작성
        String commentResult = commentWrite(token, boardId, 0.0);

        //댓글확인
        Double commentId = extractId(commentResult);

        Comment comment = commentService.getComment((int) Math.abs(commentId));

        assertNotNull(comment);
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

    public String boardWrite(String token) throws Exception {
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

        return result.getResponse().getContentAsString();
    }

    public String commentWrite(String token, Double boardId, Double commentId) throws Exception {
        StringBuilder url = new StringBuilder();
        url.append("/apis/comment/");
        url.append((int) Math.abs(boardId));
        url.append("/");
        url.append((int) Math.abs(commentId));

        String sComment = "댓글입니다.";
        MultiValueMap<String, String> commentParams = new LinkedMultiValueMap<>();
        commentParams.add("contents", sComment);

        //게시글에 댓글 입력
        MvcResult result = mockMvc.perform(post(url.toString())
                .params(commentParams)
                .header("X-AUTH-TOKEN",token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    public Double extractId(String content) {
        Gson gson = new Gson();
        SingleResult<Board> singleResult = gson.fromJson(content, SingleResult.class);
        Map map = (Map) singleResult.getData();

        return (Double) map.get("id");
    }


}