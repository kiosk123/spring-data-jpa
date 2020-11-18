package com.study.datajpa.event;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;

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
class BaseEntityTest {

    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    EntityManager em;
    
    @Test
    public void testBaseEntity() {
        //given
        Member member = new Member("member1");
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
        Member findMember = memberRepository.findById(member.getId()).get();
        
        //then
        assertNotNull(findMember.getCreateDate());
        assertNotNull(findMember.getUpdatedDate());
        assertNotNull(findMember.getCreateBy());
        assertNotNull(findMember.getLastModifiedBy());
        assertNotEquals(findMember.getCreateDate(), findMember.getUpdatedDate());
        assertNotEquals(findMember.getCreateBy(), findMember.getLastModifiedBy());
        
        System.out.println("created date : " + findMember.getCreateDate());
        System.out.println("updated date : " +findMember.getUpdatedDate());
        System.out.println("created by : " + findMember.getCreateBy());
        System.out.println("last modified by : " + findMember.getLastModifiedBy());
        
    }

}
