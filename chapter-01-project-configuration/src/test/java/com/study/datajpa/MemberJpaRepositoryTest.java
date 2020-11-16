package com.study.datajpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.MemberJpaRepository;

/**
 * 테스트시 @SpringBootApplication이 설정된 클래스의 패키와 같은 경로거나
 * 하위 경로로 패키지 경로를 맞춰줘야 실행됨
 */
@ActiveProfiles("test")
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
