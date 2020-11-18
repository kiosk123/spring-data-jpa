package com.study.datajpa.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.datajpa.domain.Item;

@Profile("test")
public interface ItemTestRepository extends JpaRepository<Item, String>{

}
