package com.study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.study.datajpa.domain.Member;
import com.study.datajpa.dto.MemberDTO;


@NamedQuery(name = "Member.findByUserName",
            query = "select m from Member m where m.userName = :userName")
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    //Optional로 반환가능함
    Optional<Member> findByUserNameAndAge(String userName, int age);
    
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);
    
    @Query(name = "Member.findByUsername")
    List<Member> findByUserName(@Param("userName") String userName);
    
    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);
    
    @Query("select m.userName from Member m")
    List<String> findUserNameList();
    
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);
    
    @Query("select new com.study.datajpa.dto.MemberDTO(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDTO> findMemberDTO();
    
    /**
     * totalCount를 가져오는 것은 데이터량이 많아지거나 조인하는 테이블이 많아지면 복잡할 수 있다.
     * totalCount는 조인하여 가져오지 않아도 되는 경우가 많기 때문에 countQuery를 분리하여 사용할 수 있다.
     */
    @Query(value = "select m from Member m left join m.team t where m.age = :age", countQuery = "select count(m.userName) from Member m where m.age = :age")
    Page<Member> findByAge(@Param("age")int age, Pageable pageable);
    
    @Query("select m from Member m where m.age = :age")
    Slice<Member> findByAgeIntoSlice(@Param("age")int age, Pageable pageable);
    
    /**
     * 벌크 수정 쿼리
     */
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age")int age);
    
    
    /*
     * JPQL, @EntityGraph, @EntityGraph + @Query, 메소드 이름 쿼리를 이용한 페치조인 처리
     */
    @Query("select m from Member m left join m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGrapthByUserName(@Param("userName") String userName);
    
    /**
     * JPA 힌트
     * @QueryHint(name = "org.hibernate.readOnly", value = "true") 설정시
     * 변경감지 체크를 안한다. 성능 최적화를 위해 스냅샷을 안 만든다.
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);
}
