package com.study.datajpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.study.datajpa.domain.Member;

@Repository
public class MemberJpaRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    public Long save(Member member) {
        em.persist(member);
        return member.getId(); 
    }
    
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
