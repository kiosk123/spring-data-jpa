package com.study.datajpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.study.datajpa.domain.Member;
import com.study.datajpa.domain.Team;
import com.study.datajpa.repository.MemberRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class NativeQueryTest {

    @Autowired
    MemberRepository memberRepository;
    
    @PersistenceContext
    EntityManager em;
    
    @Test
    void nativeQueryTest() {
        Team teamA = new Team("teamA");
        em.persist(teamA);
        
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();
        
        //when
        Member findMember = memberRepository.findByNativeQuery("m1");
        
        //then
        assertEquals(m1.getAge(), findMember.getAge());
        assertEquals(m1.getUserName(), findMember.getUserName());
    }

}
