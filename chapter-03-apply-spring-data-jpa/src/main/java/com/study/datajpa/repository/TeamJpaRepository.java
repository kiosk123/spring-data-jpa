package com.study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.study.datajpa.domain.Team;

@Repository
public class TeamJpaRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    public Long save(Team team) {
        em.persist(team);
        return team.getId();
    }
    
    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                 .getResultList();
    }
    
    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(em.find(Team.class, id));
    }
    
    public Long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                 .getSingleResult();
    }
}
