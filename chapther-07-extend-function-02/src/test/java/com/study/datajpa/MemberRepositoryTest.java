package com.study.datajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    
    @Test
    public void 회원등록() {
        Member member = new Member("memberA");
        memberRepository.save(member);
        
        Optional<Member> optional = memberRepository.findById(member.getId());
        
        Member findMember = null;
        if (optional.isPresent()) {
            findMember = optional.get();
        }
        
        assertNotNull(findMember);
        assertEquals(findMember.getId(), member.getId());
        
        // assertEquals(findMember.getUserName(), member.getUserName()); // 아래와 동일
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    }
    
    @Test
    public void findByUserNameAndAge() {
        Member member = new Member("memberA");
        member.setAge(15);
        memberRepository.save(member);
        
        Optional<Member> memberOpt = memberRepository.findByUserNameAndAge(member.getUserName(), member.getAge());
        
        if (memberOpt.isPresent()) {
            Member findMember = memberOpt.get();
            assertEquals(member.getId(), findMember.getId());
            assertEquals(member.getUserName(), findMember.getUserName());
            assertEquals(member.getAge(), findMember.getAge());
        } 
        else {
            fail("seach member by userName and age");
        }
    }
}
