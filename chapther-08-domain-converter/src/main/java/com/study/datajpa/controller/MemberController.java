package com.study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberRepository;
import com.study.datajpa.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }
    
    /**
     * 도메인 클래스 컨버팅
     * id 파라미터로 member 엔티티를 가져와서 자동으로 세팅해준다.
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUserName();
    }
    
    
    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
