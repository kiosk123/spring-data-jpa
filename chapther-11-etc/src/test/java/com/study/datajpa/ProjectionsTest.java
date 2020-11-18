package com.study.datajpa;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.domain.Team;
import com.study.datajpa.dto.UserData;
import com.study.datajpa.dto.UserNameOnly;
import com.study.datajpa.dto.UserNameOnlyDTO;
import com.study.datajpa.repository.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProjectionsTest {

    @Autowired
    MemberRepository memberRepository;
    
    @PersistenceContext
    EntityManager em;
    
    @Test
    void test() {
        Team teamA = new Team("teamA");
        em.persist(teamA);
        
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        
        em.persist(m1);
        em.persist(m2);
        
        //when 
        //메서드이름은 자유 반환타입으로 인지
        //close 프로젝션 - 필요한 필드의 데이터만 쿼리를 날려서 가져옴
        List<UserNameOnly> result = memberRepository.findProjectionsByUserName("m1");
        
        assertEquals("m1", result.get(0).getUserName());
        
        //open 프로젝션 - 엔티티를 조회한 다음 필요한 데이터를 추출해서 가져옴 
        List<UserData> datas = memberRepository.findOpenProjectionsByUserName("m1");
        String data = m1.getUserName() + " " + m1.getAge();
        assertEquals(data, datas.get(0).getUserData());
        
        
        //dto클래스를 이용한 프로젝션
        List<UserNameOnlyDTO> dtos = memberRepository.findDTOProjectionsByUserName("m1");
        assertEquals("m1", dtos.get(0).getUserName());
    } 

}
