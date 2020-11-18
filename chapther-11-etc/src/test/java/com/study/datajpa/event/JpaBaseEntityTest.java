package com.study.datajpa.event;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.MemberJpaEntity;
import com.study.datajpa.repository.MemberJpaTestRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class JpaBaseEntityTest {
    
    @Autowired
    MemberJpaTestRepository memberRepository;
    
    @Autowired
    EntityManager em;
    
    @Test
    public void testJpaBaseEntity() {
        //given
        MemberJpaEntity member = new MemberJpaEntity("member1");
        memberRepository.save(member);
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        member.setUserName("member2");
        em.flush();
        em.clear();
        
        //when
        MemberJpaEntity findMember = memberRepository.findById(member.getId()).get();
        
        //then
        assertNotNull(findMember.getCreateDate());
        assertNotNull(findMember.getUpdatedDate());
        assertNotEquals(findMember.getCreateDate(), findMember.getUpdatedDate());
        
        System.out.println("created date : " + findMember.getCreateDate());
        System.out.println("updated date : " +findMember.getUpdatedDate());
        
    }
}
