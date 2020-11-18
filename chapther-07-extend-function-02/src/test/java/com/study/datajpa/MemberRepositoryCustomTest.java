package com.study.datajpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberRepositoryCustomTest {

    @Autowired
    private MemberRepository memberRepository;
    
    @BeforeEach
    public void settingMemberData() {
                
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        Member member3 = new Member("member3", 30);
        Member member4 = new Member("member4", 40);
        Member member5 = new Member("member5", 50);
        Member member6 = new Member("member6", 60);
        Member member7 = new Member("member7", 70);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
       
    }
    
    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
        
        assertEquals(7, result.size());
    }

}
