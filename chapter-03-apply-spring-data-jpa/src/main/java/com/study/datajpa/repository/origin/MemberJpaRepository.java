package com.study.datajpa.repository.origin;

import java.util.List;
import java.util.Optional;

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
    
    public void delete(Member member) {
        em.remove(member);
    }
    
    public List<Member> findAll() {
        return em.createQuery("select m from Member", Member.class)
                 .getResultList();
    }
    
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(find(id));
    }
    
    public Long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                 .getSingleResult();
    }
}
