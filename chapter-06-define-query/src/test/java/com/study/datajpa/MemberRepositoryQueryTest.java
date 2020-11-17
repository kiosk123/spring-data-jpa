package com.study.datajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.domain.Team;
import com.study.datajpa.dto.MemberDTO;
import com.study.datajpa.repository.MemberRepository;
import com.study.datajpa.repository.TeamRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback(false)
@TestInstance(Lifecycle.PER_CLASS)
class MemberRepositoryQueryTest {
    
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    TeamRepository teamRepository;
    
    Set<String> names = new HashSet<>();
    
    @BeforeAll
    public void settingMemberData() {
        
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("AAA", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        
        names.add(member1.getUserName());
        names.add(member2.getUserName());
        names.add(member3.getUserName());
        names.add(member4.getUserName());
    }
    
    @Test
    public void findByUserNameAndGreaterThen() {

        List<Member> collect = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
    
    @Test
    public void findByUserName() {
        String userName = "member1";
        
        List<Member> collect = memberRepository.findByUserName(userName);
        
        assertEquals(1, collect.size());
        assertEquals("member1", collect.get(0).getUserName());
    }
    
    @Test 
    public void findUser() {
        List<Member> collect = memberRepository.findUser("AAA", 20);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
    
    @Test
    public void findUserNameList() {
        List<String> collect = memberRepository.findUserNameList();
       
        assertEquals(4, collect.size());
        assertEquals(4, names.size());
        collect.forEach(name -> {
            if (!names.contains(name)) {
                fail(name + " is not in findUserNameList");
            }
        });
    }
    
    @Test
    public void findMemberDTO() {
        List<MemberDTO> collect = memberRepository.findMemberDTO();
       
        assertEquals(4, collect.size());
        assertEquals(4, names.size());
        collect.forEach(dto -> {
            if (!names.contains(dto.getUserName())) {
                fail(dto.getUserName() + " is not in findMemberDTO");
            }
        });
        
    }
    
    
    @Test
    public void findByNames() {
        List<String> userNames = new ArrayList<>(names);
        
        List<Member> collect = memberRepository.findByNames(userNames);
        
        assertEquals(4, collect.size());
    }
    
    @Test
    public void findByPage() {
        /*
         * 페이지 번호(0 부터 시작), 가져올 데이터 수, 정렬 기준은 userName 프로퍼티를 기준으로 내림차순으로
         */
        int age = 10;
        Pageable pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));
        
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        
        List<Member> content = page.getContent();
        Long totalCount = page.getTotalElements();
        
        assertEquals(1, content.size());
        Member member = content.get(0);
        assertEquals("member1", member.getUserName());
        assertEquals(1, totalCount);
        
    }
}
