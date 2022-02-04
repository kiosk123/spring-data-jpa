#  Spring Data JPA의 공통 인터페이스(JpaRepository)를 이용한 리포지토리 생성

`@SpringBootApplication`이 붙은 클래스의 하위 패키지에 공통 인터페이스를 구현한 리포지토리를 생성한 것이 아니고  
공통 인터페이스를 구현한 리포지토리 패키지 구성을 다르게 가져갔다면 다음과 같이 `@EnableJpaRepositories("검색할 공통 인터페이스를 구현한 리포지토리 패키지 경로")`를 설정해주어야 한다.

```java
@EnableJpaRepositories("com.study.datajpa.repository")
@SpringBootApplication
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

}
```

`org.springframework.data.jpa.repository.JpaRepository` 인터페이스를 구현해서 리포지토리를 생성한다.  
`JpaRepository` 형태는 다음과 같다.

```java
/**
 * JpaRepository<엔티티타입, 엔티티PK타입>
 */
JpaRepository<T, ID>
```

`org.springframework.data.jpa.repository.JpaRepository` 인터페이스를 구현해서 리포지토리를 생성한다.  
이렇게 생성한 리포지토리는 `@Repository` 애너테이션을 붙이지 않아도 된다.

```java
/**
 * JpaRepository<엔티티타입, 엔티티PK타입>
 *
 */
public interface MemberRepository extends JpaRepository<Member, Long>{
    
}
```

## 순수 JPA 기반 리포지토리와 JpaRepository 인터페이스를 구현한 리포지토리 비교

**순수 JPA 기반 `MemberJpaReposiotry`**
```java
@Repository
public class MemberJpaRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    public Long save(Member member) {
        em.persist(member);
        return member.getId(); 
    }
    
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
    
    public void delete(Member member) {
        em.remove(member);
    }
    
    public List<Member> findAll() {
        return em.createQuery("select m from Member", Member.class)
                 .getResultList();
    }
    
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(find(id));
    }
    
    public Long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                 .getSingleResult();
    }
}
```

**`JpaRepository` 인터페이스를 구현한 리포지토리**
```java
/**
 * JpaRepository<엔티티타입, 엔티티PK타입>
 *
 */
public interface MemberRepository extends JpaRepository<Member, Long>{
    
}
```

## `JpaRepository`인터페이스를 구현한 리포지토리 테스트

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
}
```