package com.study.datajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
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
class MemberRepositoryQueryTest {
    
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    TeamRepository teamRepository;
    
    @PersistenceContext
    EntityManager em;
    
    Set<String> names = new HashSet<>();
    
    @BeforeEach
    public void settingMemberData() {
        
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("AAA", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("member5", 10, teamA);
        Member member6 = new Member("member6", 10, teamA);
        Member member7 = new Member("member7", 10, teamA);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
        
        names.add(member1.getUserName());
        names.add(member2.getUserName());
        names.add(member3.getUserName());
        names.add(member4.getUserName());
        names.add(member5.getUserName());
        names.add(member6.getUserName());
        names.add(member7.getUserName());
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
       
        assertEquals(7, collect.size());
        assertEquals(7, names.size());
        collect.forEach(name -> {
            if (!names.contains(name)) {
                fail(name + " is not in findUserNameList");
            }
        });
    }
    
    @Test
    public void findMemberDTO() {
        List<MemberDTO> collect = memberRepository.findMemberDTO();
       
        assertEquals(7, collect.size());
        assertEquals(7, names.size());
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
        
        assertEquals(7, collect.size());
    }
    
    /**
     * 페이징 테스트
     */
    @Test
    public void findByAge() {
        /*
         * 페이지 번호(0 부터 시작), 가져올 데이터 수, 정렬 기준은 userName 프로퍼티를 기준으로 내림차순으로
         */
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));
        
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        
        List<Member> content = page.getContent();
        Long totalCount = page.getTotalElements();
        
        
        //-- then --//
        //3개를 정확하게 가져왔는가?
        assertEquals(3, content.size()); 
        
        //전체 데이터수가 4개인가
        assertEquals(4, totalCount); 
        
        //현재 페이지 번호
        //현재 페이지번호가 0이 맞는가?
        assertEquals(0, page.getNumber()); 
        
        //전체페이지 개수
        //전체 페이지 갯수는 2개여야 된다. (전체데이터 4개에서 1페이지가 3개로 이루어짐으로)
        assertEquals(2, page.getTotalPages()); 
        
        //현재페이지가 첫번째 페이지인가
        assertThat(page.isFirst()).isTrue();
        
        //다음페이지가 있은가?
        assertThat(page.hasNext()).isTrue();
        
        //이전페이지가 존재하는가?
        assertThat(page.hasPrevious()).isFalse();
        
        //page 타입을 DTO로 변환
        Page<MemberDTO> toMap = page.map(member -> new MemberDTO(member.getId(), member.getUserName(), member.getTeam().getName()));
        
        
    }
    
    /**
     * 슬라이스 Slice 테스트
     */
    @Test
    public void findByAgeIntoSlice() {
        /*
         * 페이지 번호(0 부터 시작), 가져올 데이터 수, 정렬 기준은 userName 프로퍼티를 기준으로 내림차순으로
         */
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));
       
        Slice<Member> slice = memberRepository.findByAgeIntoSlice(age, pageRequest);
        
        List<Member> content = slice.getContent();
        
        //-- then --//
        //3개를 정확하게 가져왔는가?
        /**
         * 실제로는 select ... limit 4; 과 같이
         * 실제로는 4개를 가져옴 -> 요청한 데이터 갯수 + 1
         * 하지만 List에는 요청한 데이터 갯 수만큼의 데이터가 담김
         * 모바일의 더 보기 기능에 유용(전체 페이지 갯수를 구하지 않고, 다음페이지 존재 여부를 알 수 있기 때문)
         */
        assertEquals(3, content.size()); 
        
        //현재 페이지 번호
        //현재 페이지번호가 0이 맞는가?
        assertEquals(0, slice.getNumber()); 
        
        //현재페이지가 첫번째 페이지인가
        assertThat(slice.isFirst()).isTrue();
        
        //다음페이지가 있은가?
        assertThat(slice.hasNext()).isTrue();
        
        //이전페이지가 존재하는가?
        assertThat(slice.hasPrevious()).isFalse();
    }
    
    @Test
    public void buikAgePlus() {
        Member member = new Member("BBB", 20);
        memberRepository.save(member);
        
        int updateCount = memberRepository.bulkAgePlus(20);
        
        assertEquals(4, updateCount);
        
        Member findMember = memberRepository.findById(member.getId()).get();
        
        /**
         * 원래 나이는 20살에 20 + 1이므로 21살이 되어야한다.
         * 하지만 실제로는 20살이 나온다.
         * 그 이유는 JPQL, @NamedQuery(결국은 JPQL을 query name으로 매핑한 것)로 select하고
         * 영속성 컨텍스트에서 select로 가줘온 엔티티를 반환되지만.
         * 
         * JPQL이 아닌 EntityManager의 find()나 @Query를 통한 데이터를 가져오지 않는 쿼리 이름 메소드(ex. findById)로
         * 데이터를 가져온 경우에는 DB가 아닌 먼저 영속성 컨텍스트에 있는 것을 먼저조회하기 때문에
         * 벌크 연산 후에는 영속성 컨텍스트를 flush()와 clear()를 같이하거나 clear() 해줘야 한다.
         * 
         * 가장 좋은 건 벌크연산만 실행하고 딱 끝나는 것이 좋다.!!!
         */
        assertNotEquals(21, findMember.getAge());
        
        /**
         * 같은 트랜잭션 상에서 EntityManager의 동일성을 스프링은 보장한다.
         */
        em.clear();
        
        /**
         * 21살로 정확히 다시 조회된다.
         */
        findMember = memberRepository.findById(member.getId()).get();
        assertEquals(21, findMember.getAge());
        
    }
    
    @Test
    public void queryHint() {
        Member member = new Member("newMember", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();
        
        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUserName("member2");
        em.flush();
    }
    
}
