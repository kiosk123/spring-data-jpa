package com.study.datajpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.domain.Team;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberAndTeamTest {

    @Autowired
    EntityManager em;
    
    @Test
    public void 팀에소속된회원() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        
        em.flush();
        
        // 확인
        List<Member> collect = em.createQuery("select m from Member m join fetch m.team", Member.class)
                                 .getResultList();
        
        assertEquals(4, collect.size());
        
        Set<String> names = new HashSet<>();
        collect.forEach(m -> names.add(m.getUserName()));
        
        // 데이터 중복 체크
        assertEquals(names.size(), 4);
    }
}
