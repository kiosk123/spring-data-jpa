package com.study.datajpa.repository.redefine;

import java.util.List;

import javax.persistence.EntityManager;

import com.study.datajpa.domain.Member;

import lombok.RequiredArgsConstructor;
/**
 * JPA를 직접 사용하는 사용자정의 Member 리포지토리
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;
    
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                 .getResultList();
    }

}
