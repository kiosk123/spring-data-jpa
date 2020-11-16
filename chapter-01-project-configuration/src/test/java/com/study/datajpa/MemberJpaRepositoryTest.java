package com.study.datajpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberJpaRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberJpaRepositoryTest {
    
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @Transactional
    public void test() {
        Member member = new Member("memberA");
        memberJpaRepository.save(member);
        
        Member findMember = memberJpaRepository.find(member.getId());
        
        assertEquals(findMember.getUserName(), member.getUserName());
        assertEquals(findMember.getId(), member.getId());
    }
}
