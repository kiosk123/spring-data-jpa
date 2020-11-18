package com.study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.datajpa.domain.MemberJpaEntity;

public interface MemberJpaTestRepository extends JpaRepository<MemberJpaEntity, Long>{

}
