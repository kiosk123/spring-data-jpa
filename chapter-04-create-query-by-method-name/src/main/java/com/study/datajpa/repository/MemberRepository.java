package com.study.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.datajpa.domain.Member;

/**
 * JpaRepository<엔티티타입, 엔티티PK타입>
 *
 */
public interface MemberRepository extends JpaRepository<Member, Long>{
    
    /**
     * UserName : Member엔티티의 userName과 매핑
     * Age : Member 엔티티의 age와 매핑
     * GreaterThan : jpql 조건 >= 과 매핑
     * And : jpql조건 and와 매핑
     * findByUserNameAndGreaterThen
     * 
     * 아래와 같이 선언시 다음과 같은 JPQL이 생성됨
     * select m from Member m where m.userName = :userName and m.age > :age
     * 참고 : https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords
     */
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);
}
