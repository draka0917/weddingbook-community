package com.weddingbook.board.service;

import com.weddingbook.board.advice.exception.UserNotFoundException;
import com.weddingbook.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public UserDetails loadUserByUsername(String userPk) {
        return memberRepository.findById(Long.valueOf(userPk)).orElseThrow(UserNotFoundException::new);
    }
}
