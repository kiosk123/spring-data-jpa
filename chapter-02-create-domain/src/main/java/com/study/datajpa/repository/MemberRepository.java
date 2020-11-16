package com.study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.datajpa.domain.Member;

/**
 * JpaRepository<엔티티타입, 엔티티PK타입>
 *
 */
public interface MemberRepository extends JpaRepository<Member, Long>{
    
}
