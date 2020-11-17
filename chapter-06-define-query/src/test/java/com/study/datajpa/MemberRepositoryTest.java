package com.study.datajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback(true)
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
    public void findByUserNameAndGreaterThen() {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("AAA", 20, null);
        Member member3 = new Member("member3", 30, null);
        Member member4 = new Member("member4", 40, null);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        
        List<Member> collect = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
    
    @Test
    public void findByUserName() {
        Member member1 = new Member("member1", 10, null);
        memberRepository.save(member1);
        
        List<Member> collect = memberRepository.findByUserName(member1.getUserName());
        
        assertEquals(1, collect.size());
        assertEquals("member1", collect.get(0).getUserName());
    }
    
    @Test 
    public void findUser() {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("AAA", 20, null);
        Member member3 = new Member("member3", 30, null);
        Member member4 = new Member("member4", 40, null);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        
        List<Member> collect = memberRepository.findUser("AAA", 20);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
}
