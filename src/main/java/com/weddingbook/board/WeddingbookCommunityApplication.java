package com.weddingbook.board;

import com.weddingbook.board.advice.exception.SigninException;
import com.weddingbook.board.advice.exception.UserNotFoundException;
import com.weddingbook.board.config.security.JwtTokenProvider;
import com.weddingbook.board.entity.Board;
import com.weddingbook.board.entity.Member;
import com.weddingbook.board.repository.BoardRepository;
import com.weddingbook.board.repository.CommentRepository;
import com.weddingbook.board.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.stream.IntStream;

@EnableAspectJAutoProxy
@EnableJpaAuditing
@SpringBootApplication
public class WeddingbookCommunityApplication implements CommandLineRunner{

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	BoardRepository boardRepository;

	public static void main(String[] args) {
		SpringApplication.run(WeddingbookCommunityApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Override
	public void run(String... args) throws Exception {
		long temptime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String uid = "test" + temptime + "@naver.com";
		String password = passwordEncoder.encode("123456aa");

		//signup
		Member role_user = memberRepository.save(Member.builder()
				.uid(uid)
				.password(password)
				.roles(Collections.singletonList("ROLE_USER"))
				.build());

		IntStream.rangeClosed(1, 1024).forEach(i -> {
			Board post = new Board("안녕하세요. 반갑습니다." + i, "test", memberRepository.findByUid(uid).orElseThrow(UserNotFoundException::new));
			boardRepository.save(post);
		});
	}
}
