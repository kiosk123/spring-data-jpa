package com.study.datajpa.repository;

import java.util.List;

import javax.persistence.NamedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.datajpa.domain.Member;


@NamedQuery(name = "Member.findByUserName",
            query = "select m from Member m where m.userName = :userName")
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);
    
    @Query(name = "Member.findByUsername")
    List<Member> findByUserName(@Param("userName") String userName);
    
    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);
    
    @Query("select m.userName from Member m")
    List<String> findUserNameList();
}
