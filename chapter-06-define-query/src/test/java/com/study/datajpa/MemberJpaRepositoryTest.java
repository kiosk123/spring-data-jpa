package com.study.datajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.datajpa.domain.Member;
import com.study.datajpa.repository.origin.MemberJpaRepository;

/**
 * 테스트시 @SpringBootApplication이 설정된 클래스의 패키와 같은 경로거나 하위 경로로 패키지 경로를 맞춰줘야 실행됨
 */
@ActiveProfiles("test")
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @Transactional
    public void test() {
        Member member = new Member("memberA");
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(member.getId());

        assertEquals(findMember.getUserName(), member.getUserName());
        assertEquals(findMember.getId(), member.getId());
    }

    @Test
    @Transactional
    public void findByUserNameAndGreaterThen() {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("AAA", 20, null);
        Member member3 = new Member("member3", 30, null);
        Member member4 = new Member("member4", 40, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);

        List<Member> collect = memberJpaRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }

    @Test
    @Transactional
    public void paging() throws Exception {
        // given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        
        int age = 10;
        int offset = 0;
        int limit = 3;
        
        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);
        
        // 페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..
        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }
}
