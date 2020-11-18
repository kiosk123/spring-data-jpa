package com.study.datajpa.controller;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberRepository memberRepository;
    
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
    
    /**
     *  MVC에서 페이징처리 
     *  page - 데이터를 가져올 page 번호(0번 부터 시작), page만 지정한 경우 한번에 20개씩 가져온다(아무것도 지정안할시 첫번째 페이지 번호로 설정)
     *  sort - 특정 컬럼을 기준으로 정렬하는데 사용한다. sort=정렬대상프로퍼티명,<asc,desc>형태로 사용한다.
     *  size - 한페이지에 노출할 데이터 건수
     *  ex) page=1&sort=id,desc&sort=userName,desc
     */
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 15, sort = {"userName"})Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
    
    /**
     * 데이터 세팅
     */
    @PostConstruct
    public void init() {
        IntStream.range(0, 100).forEach(n -> {
            memberRepository.save(new Member("user" + n, n, null));
        });
    }
}
