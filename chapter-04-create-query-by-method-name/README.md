# 메소드 이름으로 쿼리 생성
  
- [쿼리 메소드 키워드 참고 1.](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords)
- [쿼리 메소드 키워드 참고 2.](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result)

## 순수 JPA 코드와 쿼리 메소드 코드 비교

**순수 JPA 코드**
```java
public List<Member> findByUserNameAndAgeGreaterThan(String userName, int age) {
    return em.createQuery("select m from Member m where m.userName = :userName and m.age > :age", Member.class)
                .setParameter("userName", userName)
                .setParameter("age", age)
                .getResultList();
}
```

**쿼리 메소드 코드**
```java
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

```

## 테스트 코드

```java
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원등록() {
        Member member = new Member("memberA");
        memberRepository.save(member);
        
        Optional<Member> optional = memberRepository.findById(member.getId());
        
        Member findMember = null;
        if (optional.isPresent()) {
            findMember = optional.get();
        }
        
        assertNotNull(findMember);
        assertEquals(findMember.getId(), member.getId());
        
        // assertEquals(findMember.getUserName(), member.getUserName()); // 아래와 동일
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    }
    
    @Test
    public void findByUserNameAndGreaterThen() {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("AAA", 20, null);
        Member member3 = new Member("member3", 30, null);
        Member member4 = new Member("member4", 40, null);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        
        List<Member> collect = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
}
```