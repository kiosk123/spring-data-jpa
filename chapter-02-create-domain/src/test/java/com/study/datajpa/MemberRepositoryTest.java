package com.study.datajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
@Rollback(false)
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
}
