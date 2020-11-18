package com.study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.datajpa.domain.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{

}
