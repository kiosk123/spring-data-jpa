package com.study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
   
}
