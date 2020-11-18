package com.study.datajpa.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.datajpa.domain.MemberJpaEntity;

@Profile("test")
public interface MemberJpaTestRepository extends JpaRepository<MemberJpaEntity, Long>{

}
